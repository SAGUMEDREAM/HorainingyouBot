package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command
public class CommandWholeMute implements CommandEntrypoint {

    @Autowired
    GroupManagerImpl groupManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("whole-mute")
                        .withPredicate(ContextPredicate.BOT_OR_GROUP)
                        .withArguments("#{group} #{enable}")
                        .withAliasName("开关全员禁言")
                        .withDefaultArgument("enable", true)
                        .withExecutor((bot, event, args) -> {
                            Optional<Group> groupOptional = args.getGroup("group");
                            boolean enable = args.getBoolean("enable");
                            if (groupOptional.isEmpty()) {
                                return;
                            }
                            Group group = groupOptional.get();
                            if (group.hasGroupPermission(bot)) {
                                group.muteForWhole(bot, enable);
                                MsgTool.reply(bot, event, "已%s全员禁言".formatted(enable ? "开启" : "关闭"));
                            }
                        })
                        .addNode("self")
                        .withPredicate(ContextPredicate.BOT_OR_GROUP)
                        .withArguments("#{enable}")
                        .withDefaultArgument("enable", true)
                        .withExecutor((bot, event, args) -> {
                            boolean enable = args.getBoolean("enable");
                            Long groupId = event.getGroupId();
                            if (groupId == null) {
                                MsgTool.reply(bot, event, "请在群聊中操作");
                            }
                            Group group = this.groupManager.getOrCreate(event);
                            if (group.hasGroupPermission(bot)) {
                                bot.setGroupWholeBan(event.getGroupId(), enable);
                                MsgTool.reply(bot, event, "已%s全员禁言".formatted(enable ? "开启" : "关闭"));
                            }
                        })
                        .up()
        );
    }
}
