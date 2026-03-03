package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command
public class CommandUnmute implements CommandEntrypoint {

    @Autowired
    GroupManagerImpl groupManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("unmute")
                        .withPredicate(ContextPredicate.BOT_OR_GROUP)
                        .withArguments("#{group} #{user}")
                        .withAliasName("解除禁言")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            Optional<Group> groupOptional = args.getGroup("group");
                            if (userOptional.isEmpty() || groupOptional.isEmpty()) {
                                return;
                            }
                            User user = userOptional.get();
                            Group group = groupOptional.get();
                            if (group.hasMember(bot, user) && group.hasGroupPermission(bot)) {;
                                group.mute(bot, user, 0);
                                MsgTool.reply(bot, event, "已解除 %s 的禁言".formatted(user.getUserId()));
                            }
                        })
                        .addNode("member")
                        .withArguments("#{user}")
                        .withAliasName("成员")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            Long groupId = event.getGroupId();
                            if (groupId == null) {
                                MsgTool.reply(bot, event, "请在群聊中操作");
                            }
                            Group group = this.groupManager.getOrCreate(event);
                            if (!group.hasGroupPermission(bot)) {
                                return;
                            }
                            userOptional.ifPresent(user -> {
                                bot.setGroupBan(event.getGroupId(), user.getUserId(), 0);
                                MsgTool.reply(bot, event, "已解除 %s 的禁言".formatted(user.getUserId()));
                            });
                        })
                        .up()
        );
    }
}
