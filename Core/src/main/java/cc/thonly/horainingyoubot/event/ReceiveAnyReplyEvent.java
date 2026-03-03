package cc.thonly.horainingyoubot.event;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReceiveAnyReplyEvent {
    Bot bot;
    AnyMessageEvent event;
    Long targetId;
    Long senderId;
}
