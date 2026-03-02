package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command
public class CommandMute implements CommandEntrypoint {

    @Autowired
    GroupManagerImpl groupManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("mute")
                        .withPredicate(ContextPredicate.BOT_OR_GROUP)
                        .withArguments("#{group} #{user} #{duration}")
                        .withAliasName("禁言")
                        .withDefaultArgument("duration", 30 * 60)
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            Optional<Group> groupOptional = args.getGroup("group");
                            int duration = args.getInt("duration");
                            if (userOptional.isEmpty() || groupOptional.isEmpty()) {
                                return;
                            }
                            User user = userOptional.get();
                            Group group = groupOptional.get();
                            if (group.hasMember(bot, user) && group.hasGroupPermission(bot)) {;
                                group.mute(bot, user, duration);
                                MsgTool.reply(bot, event, "已将 %s 禁言 %s 秒".formatted(user.getUserId(), duration));
                            }
                        })
                        .addNode("member")
                        .withArguments("#{user} #{duration}")
                        .withAliasName("成员")
                        .withDefaultArgument("duration", 30 * 60)
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            int duration = args.getInt("duration");
                            Long groupId = event.getGroupId();
                            if (groupId == null) {
                                MsgTool.reply(bot, event, "请在群聊中操作");
                            }
                            Group group = this.groupManager.getOrCreate(event);
                            if (!group.hasGroupPermission(bot)) {
                                return;
                            }
                            userOptional.ifPresent(user -> {
                                bot.setGroupBan(event.getGroupId(), user.getUserId(), duration);
                                MsgTool.reply(bot, event, "已将 %s 禁言 %s 秒".formatted(user.getUserId(), duration));
                            });
                        })
                        .up()
        );
    }
}
