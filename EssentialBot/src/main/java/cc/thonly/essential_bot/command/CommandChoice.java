package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.horainingyoubot.util.Mth;

import java.util.HashSet;
import java.util.Set;

@Command
public class CommandChoice implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("选择")
                        .withAliasName("choice")
                        .withArguments("#{element|element} #{count}")
                        .withDefaultArgument("count", 1)
                        .withExecutor((bot, event, args) -> {
                            String elements = args.getString("element|element");
                            int count = args.getInt("count");
                            String[] split = elements.split("\\|");
                            if (split.length < count) {
                                MsgTool.reply(bot, event, "数量不足!");
                                return;
                            }

                            Set<String> chooseSet = new HashSet<>();

                            while (chooseSet.size() < count) {
                                String selected = Mth.getRandomElement(split);
                                chooseSet.add(selected);

                                if (chooseSet.size() == split.length) {
                                    break;
                                }
                            }
                            MsgTool.reply(bot, event, "选择的元素: " + String.join(", ", chooseSet));

                        })
        );

    }
}