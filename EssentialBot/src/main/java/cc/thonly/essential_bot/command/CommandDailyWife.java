package cc.thonly.essential_bot.command;

import cc.thonly.essential_bot.service.WifeServiceImpl;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

@Command
public class CommandDailyWife implements CommandEntrypoint {
    Random random = new Random();

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    WifeServiceImpl wifeService;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("抽老婆")
                        .withExecutor((bot, event, args) -> {
                            User user = this.userManager.getOrCreate(event);
                            Long groupId = event.getGroupId();
                            if (groupId == null) {
                                MsgTool.reply(bot, event, "这里不是群聊哦");
                                return;
                            }
                            ActionList<GroupMemberInfoResp> memberInfoRespList = bot.getGroupMemberList(groupId, false);
                            if (memberInfoRespList.getRetCode() != 0) {
                                MsgTool.reply(bot, event, "抽老婆失败！");
                                return;
                            }
                            if (!this.wifeService.allowGetNewWife(user)) {
                                MsgTool.reply(bot, event, ArrayMsgUtils.builder().text("今天你抽太多了哦！").build());
                                return;
                            }
                            List<GroupMemberInfoResp> data = memberInfoRespList.getData();
                            GroupMemberInfoResp result = null;
                            int cnt = 0;
                            while (true) {
                                GroupMemberInfoResp resp = data.get(this.random.nextInt(data.size()));
                                if (this.wifeService.addWife(user, resp.getUserId())) {
                                    result = resp;
                                    break;
                                }
                                cnt++;
                                if (cnt >= 8) {
                                    break;
                                }
                            }
                            if (result == null) {
                                MsgTool.reply(bot, event, "抽老婆失败！");
                                return;
                            }
                            MsgTool.reply(bot, event, ArrayMsgUtils.builder()
                                    .at(event.getUserId())
                                    .text(" 你今天的群老婆是\n")
                                    .img(MsgTool.getImageFromUrl(MsgTool.getUserAvatar(result.getUserId())))
                                    .text("\n亲爱的%s".formatted(result.getNickname()))
                                    .build()
                            );
                        })
        );

    }
}