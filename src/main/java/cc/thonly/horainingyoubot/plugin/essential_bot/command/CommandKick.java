package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.util.MsgTool;

import java.util.Optional;

@Command
public class CommandKick implements CommandEntrypoint {

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("kick")
                        .withPredicate(ContextPredicate.BOT_OR_GROUP)
                        .withArguments("#{group} #{user} #{reject_add_request}")
                        .withDefaultArgument("reject_add_request", false)
                        .withAliasName("踢出")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            Optional<Group> groupOptional = args.getGroup("group");
                            boolean rejectAddRequest = args.getBoolean("reject_add_request");
                            if (userOptional.isEmpty() || groupOptional.isEmpty()) {
                                return;
                            }
                            User user = userOptional.get();
                            Group group = groupOptional.get();
                            if (group.hasMember(bot, user) && group.hasGroupPermission(bot)) {
                                group.kick(bot, user, rejectAddRequest);
                                MsgTool.reply(bot, event, "已将 %s %s移出群聊 %s".formatted(user.getUserId(), rejectAddRequest ? "永久" : "", group.getGroupId()));
                            }
                        })
                        .addNode("member")
                        .withArguments("#{user} #{reject_add_request}")
                        .withDefaultArgument("reject_add_request", false)
                        .withAliasName("成员")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            boolean rejectAddRequest = args.getBoolean("reject_add_request");
                            Long groupId = event.getGroupId();
                            if (groupId == null) {
                                MsgTool.reply(bot, event, "请在群聊中操作");
                            }
                            userOptional.ifPresent(user -> {
                                bot.setGroupKick(event.getGroupId(), user.getUserId(), rejectAddRequest);
                                MsgTool.reply(bot, event, "已将 %s %s移出群聊 %s".formatted(user.getUserId(), rejectAddRequest ? "永久" : "", event.getGroupId()));
                            });
                        })
                        .up()
        );
    }
}
