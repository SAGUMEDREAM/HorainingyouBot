package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;

@Command
public class CommandRandomAnime implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机动漫图")
                        .withAliasName("随机二次元")
                        .withExecutor((bot, event, args) -> {
                            bot.sendMsg(event,
                                    ArrayMsgUtils.builder()
                                            .reply(event.getMessageId())
                                            .img("https://www.hhlqilongzhu.cn/api/tu_yitu.php")
                                            .build(),
                                    false
                            );
                        })
        );
    }
}
