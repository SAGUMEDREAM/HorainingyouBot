package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.service.DataManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupInfoResp;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Command
public class CommandCopyGroupMembers implements CommandEntrypoint {

    @Autowired
    DataManagerImpl dataManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("备份群员")
                        .withPermissionLevel(2)
                        .withArguments("#{group_id} #{cache}")
                        .withDefaultArgument("cache", false)
                        .withExecutor((bot, event, args) -> {
                            long groupId = args.getLong("group_id");
                            boolean cache = args.getBoolean("cache");
                            ActionData<GroupInfoResp> groupInfo = bot.getGroupInfo(groupId, cache);
                            ActionList<GroupMemberInfoResp> actionList = bot.getGroupMemberList(groupId, cache);
                            List<GroupMemberInfoResp> groupMemberList = actionList.getData();
                            if (groupMemberList == null) {
                                return;
                            }
                            StringBuilder sb = new StringBuilder("群名称：%s\n群号：%s\n====群员列表====\n".formatted(groupInfo.getData().getGroupName(), groupId));
                            for (GroupMemberInfoResp groupMemberInfoResp : groupMemberList) {
                                String nickname = groupMemberInfoResp.getNickname();
                                Long userId = groupMemberInfoResp.getUserId();
                                String role = groupMemberInfoResp.getRole();
                                Integer joinTime = groupMemberInfoResp.getJoinTime();
                                sb.append("名称：%s\nQQ号：%s\n角色：%s\n加入时间：%s\n======\n".formatted(nickname, userId, role, joinTime));
                            }
                            this.dataManager.save("%s_group_info.txt".formatted(groupId), sb.toString().getBytes(StandardCharsets.UTF_8));
                            MsgTool.reply(bot, event, "已保存至 /data/%s_group_info.txt".formatted(groupId));
                        })
        );

    }
}