package cc.thonly.horainingyoubot.event;

import com.mikuac.shiro.core.Bot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupKickEvent {
    Bot bot;
    Object event;
}
