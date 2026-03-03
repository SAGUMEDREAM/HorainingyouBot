package cc.thonly.horainingyoubot.command;

import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import com.mikuac.shiro.core.Bot;

import java.util.Objects;

@FunctionalInterface
public interface ContextPredicate {
    ContextPredicate BOT_OR_GROUP = (bot, user, group) ->
            (user != null && (user.hasPermissionLevel(2) || Objects.equals(user.getUserId(), bot.getSelfId()))) || (group != null && group.hasGroupPermission(bot, user));
    ContextPredicate BOT_ONLY = (bot, user, group) ->
            (user != null && (user.hasPermissionLevel(2) || Objects.equals(user.getUserId(), bot.getSelfId())));
    ContextPredicate GROUP_ONLY = (bot, user, group) ->
            (group != null && group.hasGroupPermission(bot, user));

    boolean test(Bot bot, User user, Group group);
}
