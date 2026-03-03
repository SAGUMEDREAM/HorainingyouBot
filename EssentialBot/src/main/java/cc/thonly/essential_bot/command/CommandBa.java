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
public class CommandBa implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("ba")
                        .withAliasName("BA")
                        .withArguments("#{start_text} #{end_text}")
                        .withExecutor((bot, event, args) -> {
                            String startText = args.getString("start_text");
                            String endText = args.getString("end_text");
                            if (ValueAssert.isNull(startText, endText)) {
                                return;
                            }
                            String api = "https://oiapi.net/API/BlueArchive?startText=" + MsgTool.encodeURIComponent(startText) + "&endText=" + MsgTool.encodeURIComponent(endText);
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