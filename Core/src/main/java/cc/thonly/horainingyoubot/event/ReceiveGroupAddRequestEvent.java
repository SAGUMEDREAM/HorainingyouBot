package cc.thonly.horainingyoubot.event;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReceiveGroupAddRequestEvent {
    Bot bot;
    GroupAddRequestEvent event;
}
