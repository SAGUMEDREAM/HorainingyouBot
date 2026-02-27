package cc.thonly.horainingyoubot.command.internal;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.repository.UserRepository;
import cc.thonly.horainingyoubot.service.MessageHandlerImpl;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@Command
public class CommandExecute implements CommandEntrypoint {

    @Autowired
    Commands commands;

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageHandlerImpl messageHandler;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("execute")
                        .withPermissionLevel(PermissionLevel.ADMIN)
                        .withArguments("#{user} #{command}")
                        .withExecutor((bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            if (userOptional.isEmpty()) {
                                return;
                            } else {
                                String command = args.getString("command");
                                if (command == null) {
                                    return;
                                }
                                User user = userOptional.get();
                                GroupMessageEvent.GroupMessageEventBuilder<?, ?> temp = event.toBuilder();
                                GroupMessageEvent.GroupSender sender = event.getSender();
                                sender.setUserId(user.getUserId());
                                temp.userId(user.getUserId());
                                temp.message(command);
                                temp.arrayMsg(ArrayMsgUtils.builder().text(command).build());
                                temp.sender(sender);
                                temp.messageId(event.getMessageId());
                                GroupMessageEvent fakeEvent = temp.build();
                                this.messageHandler.acceptFake(bot, user, fakeEvent);
                            }
                        })
                        .up()
        );
    }
}
