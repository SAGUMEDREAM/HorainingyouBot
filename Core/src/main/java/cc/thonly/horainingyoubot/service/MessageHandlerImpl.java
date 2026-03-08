package cc.thonly.horainingyoubot.service;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.command.internal.CommandEula;
import cc.thonly.horainingyoubot.core.CoreEvent;
import cc.thonly.horainingyoubot.data.CommandResult;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@SuppressWarnings("ALL")
@Service
@Slf4j
public class MessageHandlerImpl {
    @Autowired
    Commands commands;

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    CoreEvent coreEvent;

    public CommandResult accept(Bot bot, @Nullable User user, @Nullable Group group, AnyMessageEvent event) {
        CommandSession commandSession = this.commands.parseForCommand(bot, event);
        if (commandSession == null) {
            return CommandResult.PASS;
        }
        CommandNode node = commandSession.node();
        CommandArgs arguments = commandSession.arguments();
        CommandExecutor executor = node.getExecutor();
        if (executor == null) {
            return CommandResult.PASS;
        }
        if (user != null && (!node.hasPermissionLevel(user) && Objects.equals(node.getPredicate().test(bot, user, group), false))) {
            return CommandResult.NO_PERMISSION;
        }
        if (!user.hasAcceptedEula() && !node.isEulaNoCheck()) {
            bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text(String.join("", CommandEula.getText())).build(), false);
            return CommandResult.PASS;
        }
        try {
            executor.execute(bot, event, arguments);
        } catch (Exception e) {
            log.error("Error in handle command {}: ", event.getRawMessage(), e);
        }
        return CommandResult.SUCCESS;
    }

    public CommandResult acceptFake(Bot bot, @Nullable User user, GroupMessageEvent event) {
        CommandSession commandSession = this.commands.parseForCommand(bot, event);
        if (commandSession == null) {
            return CommandResult.PASS;
        }
        CommandNode node = commandSession.node();
        CommandArgs arguments = commandSession.arguments();
        CommandExecutor executor = node.getExecutor();
        if (executor == null) {
            return CommandResult.PASS;
        }
        try {
            executor.execute(bot, event, arguments);
        } catch (Exception e) {
            log.error("Error in handle command {}: ", event.getRawMessage(), e);
        }
        return CommandResult.SUCCESS;
    }
}
