package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.Mth;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;

@Command
public class CommandRandomUUID implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机UUID")
                        .withAliasName("随机uuid")
                        .withAliasName("生成uuid")
                        .withAliasName("生成UUID")
                        .withExecutor((bot, event, args) -> {
                            bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text(Mth.generateUUID()).build(), false);
                        })
        );

    }

}