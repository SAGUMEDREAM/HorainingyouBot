package cc.thonly.horainingyoubot.event;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.notice.GroupAdminNoticeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupAdminEvent {
    Bot bot;
    GroupAdminNoticeEvent event;
}
