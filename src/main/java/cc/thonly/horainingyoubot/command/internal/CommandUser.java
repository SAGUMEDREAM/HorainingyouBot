package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.data.db.CustomData;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command
public class CommandUser implements CommandEntrypoint {
    @Autowired
    UserManagerImpl userManager;

    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("user")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .addNode("get", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            String keyName = args.getString("key");
                            if (keyName == null) {
                                bot.sendMsg(event, "缺少参数 key", false);
                                return;
                            }
                            userOptional.ifPresentOrElse(user -> {
                                CustomData customData = user.getCustomData();
                                Object any = customData.getAny(keyName);
                                bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("获取结果：%s[{%s: %s}]".formatted(user.getUsername(), keyName, any)).build(), false);
                            }, () -> bot.sendMsg(event, "无法获取用户", false));
                        })
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .withArguments("#{user} #{key}")
                        .up()
                        .addNode("modify", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            String keyName = args.getString("key");
                            if (keyName == null) {
                                bot.sendMsg(event, "缺少参数 key", false);
                                return;
                            }
                            String typeName = args.getString("type");
                            Object value;
                            switch (typeName.toLowerCase()) {
                                case "int", "long" -> value = args.getLong("value");
                                case "float", "double" -> value = args.getDouble("value");
                                case "string" -> value = args.getString("value");
                                case "null" -> value = null;
                                default -> {
                                    return;
                                }
                            }
                            Object finalValue = value;
                            userOptional.ifPresentOrElse(user -> {
                                CustomData customData = user.getCustomData();
                                if (finalValue == null) {
                                    customData.remove(keyName);
                                } else {
                                    customData.put(keyName, finalValue);
                                }
                                bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("已设置：%s[{%s: %s}]".formatted(user.getUsername(), keyName, finalValue)).build(), false);
                                this.userManager.save(user);
                            }, () -> bot.sendMsg(event, "无法获取用户", false));
                        })
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .withArguments("#{user} #{key} #{value} #{type}")
                        .up()
                        .addNode("accept", (bot, event, args) -> {
                            String flag = args.getString("flag");
                            if (flag == null) {
                                return;
                            }
                            bot.setFriendAddRequest(flag, false, "");
                        })
                        .withArguments("#{flag}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("reject", (bot, event, args) -> {
                            String flag = args.getString("flag");
                            if (flag == null) {
                                return;
                            }
                            bot.setFriendAddRequest(flag, false, "");
                        })
                        .withArguments("#{flag}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("ban", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            userOptional.ifPresentOrElse(user -> {
                                user.setBanned(true);
                                bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("已封禁用户 %s".formatted(user.getUserId())).build(), false);
                                this.userManager.save(user);
                            }, () -> bot.sendMsg(event, "无法获取用户", false));
                        })
                        .withArguments("#{user}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("pardon", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            userOptional.ifPresentOrElse(user -> {
                                user.setBanned(false);
                                bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("已解封用户 %s".formatted(user.getUserId())).build(), false);
                                this.userManager.save(user);
                            }, () -> bot.sendMsg(event, "无法获取用户", false));
                        })
                        .withArguments("#{user}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .addNode("to-string", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            userOptional.ifPresentOrElse(user -> {
                                bot.sendMsg(event, ArrayMsgUtils.builder().text(user.toString()).build(), false);
                            }, () -> bot.sendMsg(event, "无法获取用户", false));
                        })
                        .withArguments("#{user}")
                        .withPermissionLevel(PermissionLevel.MODERATOR)
                        .up()
                        .up()
        );
    }
}
