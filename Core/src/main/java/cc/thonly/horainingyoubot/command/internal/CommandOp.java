package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command
public class CommandOp implements CommandEntrypoint {
    @Autowired
    UserManagerImpl userManager;

    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("op")
                        .withArguments("#{user} #{level}")
                        .withPermissionLevel(PermissionLevel.OWNER)
                        .withExecutor((bot, event, args) -> {
                            if (args.size() < 1) {
                                bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("参数缺失").build(), false);
                            }
                            Optional<User> userOption = args.getUser("user");
                            int level = args.getInt("level", 3);
                            userOption.ifPresent(user -> {
                                user.setPermissionLevel(level);
                                bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("已将用户 %s 的权限设置为了 %d".formatted(user.getUserId(), level)).build(), false);
                                this.userManager.save(user);
                            });
                        })
        );
    }
}
