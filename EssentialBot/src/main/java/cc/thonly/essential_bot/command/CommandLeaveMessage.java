package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

@Command
public class CommandLeaveMessage implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("留言")
                        .withExecutor((bot, event, args) -> {
                            LinkedMessage.start(bot, event, ctx -> {
                                bot.sendMsg(event, "请输入留言内容", false);
                                AnyMessageEvent content = ctx.waitNext();
                                bot.sendGroupMsg(863842932, "接收到来自 %s 的留言".formatted(event.getUserId()), false);
                                bot.sendGroupMsg(863842932, content.getArrayMsg(), false);
                                MsgTool.reply(bot, event, "留言发送成功");
                            });
                        })
        );
    }
}
