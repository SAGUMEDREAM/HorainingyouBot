package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.essential_bot.util.HomoNumber;
import cc.thonly.horainingyoubot.util.MsgTool;

@Command
public class CommandHomo implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("恶臭论证")
                        .withArguments("#{number}")
                        .withExecutor((bot, event, args) -> {
                            double number = args.getDouble("number");
                            MsgTool.reply(bot, event, HomoNumber.homo(number));
                        })
        );

    }
}