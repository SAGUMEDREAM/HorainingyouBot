package cc.thonly.horainingyoubot.command;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;

public interface CommandExecutor {
    void execute(Bot bot, AnyMessageEvent event, CommandArgs args);

    default void execute(Bot bot, GroupMessageEvent event, CommandArgs args) {
        AnyMessageEvent fakeEvent = new AnyMessageEvent();
        fakeEvent.setUserId(event.getUserId());
        fakeEvent.setGroupId(event.getGroupId());
        fakeEvent.setMessage(event.getMessage());
        fakeEvent.setMessageId(event.getMessageId());
        GroupMessageEvent.GroupSender groupSender = new GroupMessageEvent.GroupSender();
        groupSender.setUserId(event.getUserId());
        fakeEvent.setSender(groupSender);
        this.execute(bot, fakeEvent, args);
    }
}
