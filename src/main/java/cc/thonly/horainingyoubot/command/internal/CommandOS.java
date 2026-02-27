package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.BotStatus;
import cc.thonly.horainingyoubot.util.MsgUtil;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Command
public class CommandOS implements CommandEntrypoint {

    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("os")
                        .withExecutor((bot, event, args) -> {
                            BotStatus botStatus = new BotStatus();
                            CompletableFuture.supplyAsync(botStatus::getStatusMarkdown)
                                    .thenAccept(mdList -> {
                                        MarkdownImage markdownImage = this.markdownImageFactory.render(mdList);
                                        MsgUtil.reply(bot, event, ArrayMsgUtils.builder().img(markdownImage.get()).build());
                                    });
                        })
        );
    }


}
