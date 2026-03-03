package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.browser.MarkdownImage;
import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CommandMarkdown implements CommandEntrypoint {
    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("生成markdown")
                        .withArguments("#{source_code}")
                        .withExecutor((bot, event, args) -> {
                            String src = args.getString("source_code");
                            MarkdownImage render = this.markdownImageFactory.render(src);
                            MsgTool.reply(bot, event, MsgTool.img(render.get()));
                        })
        );

    }
}