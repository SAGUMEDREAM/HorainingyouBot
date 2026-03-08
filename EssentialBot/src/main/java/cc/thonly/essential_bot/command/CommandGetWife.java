package cc.thonly.essential_bot.command;

import cc.thonly.essential_bot.data.WifeData;
import cc.thonly.essential_bot.service.WifeServiceImpl;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Command
public class CommandGetWife implements CommandEntrypoint {

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    WifeServiceImpl wifeService;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("老婆列表")
                        .withExecutor((bot, event, args) -> {
                            User user = this.userManager.getOrCreate(event);
                            WifeData wifeData = this.wifeService.getOrCreate(user);
                            List<Long> wifeIdList = wifeData.getWifeList();
                            List<User> wifeUsrList = wifeIdList.stream().map(wifeUsrId -> this.userManager.getOrCreate(bot, wifeUsrId)).toList();
                            ArrayMsgUtils builder = ArrayMsgUtils.builder();
                            builder.text("你当前群老婆列表：\n");
                            for (int i = 0; i < wifeUsrList.size(); i++) {
                                User wifeUsr = wifeUsrList.get(i);
                                String str = "%s.\n%s\n".formatted(i, wifeUsr.getUsername());
                                if (i < wifeUsrList.size() - 1) {
                                    str += "\n";
                                }
                                builder.text(str);
                            }
                            MsgTool.reply(bot, event, builder.build());
                        })
        );

    }
}