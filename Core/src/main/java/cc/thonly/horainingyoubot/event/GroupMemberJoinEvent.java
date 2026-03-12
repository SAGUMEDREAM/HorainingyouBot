package cc.thonly.horainingyoubot.event;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupMemberJoinEvent {
    Bot bot;
    GroupIncreaseNoticeEvent event;
}
