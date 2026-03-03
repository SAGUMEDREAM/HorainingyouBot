package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.horainingyoubot.util.ValueAssert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Command
public class Command5k implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("5k")
                        .withAliasName("5K")
                        .withArguments("#{top} #{bottom}")
                        .withExecutor((bot, event, args) -> {
                            String top = args.getString("top");
                            String bottom = args.getString("bottom");
                            if (ValueAssert.isNull(top, bottom)) {
                                return;
                            }
                            String api = "https://gsapi.cbrx.io/image?top="+ MsgTool.encodeURIComponent(top) +"&bottom="+ MsgTool.encodeURIComponent(bottom) +"&noalpha=true";
                            try {
                                MsgTool.reply(bot, event, MsgTool.img2Byte(api));
                            } catch (Exception e) {
                                MsgTool.reply(bot, event, "生成图片失败，请稍后再试。");
                                log.error("Error: ", e);
                            }
                        })
        );

    }
}