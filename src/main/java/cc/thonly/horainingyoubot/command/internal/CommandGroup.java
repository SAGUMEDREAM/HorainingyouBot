package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.util.MsgUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command
public class CommandGroup implements CommandEntrypoint {
    @Autowired
    GroupManagerImpl groupManager;

    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("group")
                        .addNode("ban", (bot, event, args) -> {
                            Optional<Group> groupOptional = args.getGroup("group_id");
                            groupOptional.ifPresentOrElse(group -> {
                                group.setBanned(true);
                                this.groupManager.save(group);
                            }, () -> {
                                MsgUtil.reply(bot, event, "无法找到群组 %s".formatted(event.getGroupId()));
                            });
                        })
                        .withArguments("#{group_id}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("pardon", (bot, event, args) -> {
                            Optional<Group> groupOptional = args.getGroup("group_id");
                            groupOptional.ifPresentOrElse(group -> {
                                group.setBanned(false);
                                this.groupManager.save(group);
                            }, () -> {
                                MsgUtil.reply(bot, event, "无法找到群组 %s".formatted(event.getGroupId()));
                            });
                        })
                        .withArguments("#{group_id}")

                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("accept", (bot, event, args) -> {
                            String flag = args.getString("flag");
                            if (flag == null) {
                                return;
                            }
                            String sub_type = args.getString("sub_type");
                            if (sub_type == null) {
                                return;
                            }
                            String reason = args.getString("reason");
                            bot.setGroupAddRequest(flag, sub_type, false, reason);
                        })
                        .withArguments("#{flag} #{sub_type}")
                        .withDefaultArgument("reason", "")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("reject", (bot, event, args) -> {
                            String flag = args.getString("flag");
                            if (flag == null) {
                                return;
                            }
                            String sub_type = args.getString("sub_type");
                            if (sub_type == null) {
                                return;
                            }
                            String reason = args.getString("reason");
                            bot.setGroupAddRequest(flag, sub_type, false, reason);
                        })
                        .withArguments("#{flag} #{sub_type} #{reason}")
                        .withDefaultArgument("reason", "")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("exit", (bot, event, args) -> {
                            Optional<Group> groupOptional = args.getGroup("group_id");
                            groupOptional.ifPresentOrElse(group -> {
                                bot.setGroupLeave(group.getGroupId(), false);
                            }, () -> {
                                MsgUtil.reply(bot, event, "无法找到群组 %s".formatted(event.getGroupId()));
                            });
                        })
                        .withArguments("#{group_id}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .up()
        );
    }
}
