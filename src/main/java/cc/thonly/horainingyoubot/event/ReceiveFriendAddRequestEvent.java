package cc.thonly.horainingyoubot.event;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReceiveFriendAddRequestEvent {
    Bot bot;
    FriendAddRequestEvent event;
}
