package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.Mth;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;

@Command
public class CommandRandomNumber implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机数字")
                        .withArgumentName("#{max} #{min}")
                        .withDefaultArgument("max", 100)
                        .withDefaultArgument("min", 0)
                        .withExecutor((bot, event, args) -> {
                            int max = args.getInt("max");
                            int min = args.getInt("min");
                            bot.sendMsg(event, ArrayMsgUtils.builder()
                                    .reply(event.getMessageId())
                                    .text(String.valueOf(Mth.getRandomNum(max, min)))
                                    .build(), false);
                        })
        );
    }
}