package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.Mth;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;

import java.util.List;

@Command
public class CommandRandomMember implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机群友")
                        .withExecutor((bot, event, args) -> {
                            if (event.getGroupId() == null) {
                                bot.sendMsg(event, "这里不是群聊!", false);
                                return;
                            }
                            ActionList<GroupMemberInfoResp> actionList = bot.getGroupMemberList(event.getGroupId());
                            List<GroupMemberInfoResp> groupMemberInfo = actionList.getData();
                            GroupMemberInfoResp randomElement = Mth.getRandomElement(groupMemberInfo);
                            if (randomElement == null) {
                                return;
                            }
                            Long userId = randomElement.getUserId();
                            bot.sendMsg(event, ArrayMsgUtils.builder()
                                    .reply(event.getMessageId())
                                    .text("你的群友: ")
                                    .at(userId)
                                    .build(), false
                            );
                        })
        );
    }
}
