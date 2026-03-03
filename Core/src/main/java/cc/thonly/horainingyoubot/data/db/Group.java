package cc.thonly.horainingyoubot.data.db;

import cc.thonly.horainingyoubot.converter.CustomDataConverter;
import cc.thonly.horainingyoubot.data.CustomDataView;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Group {
    @Id
    private Long groupId = -1L;
    private Boolean banned;
    private List<String> permissions = new ArrayList<>();

    @Convert(converter = CustomDataConverter.class)
    private CustomData customData = new CustomData();

    public <R> R getView(CustomDataView<Group,R> factory) {
        return factory.create(this, this.customData);
    }

    public void mute(Bot bot, User user) {
        if (!this.hasGroupPermission(bot)) {
            return;
        }
        bot.setGroupBan(this.groupId, user.getUserId(), 30 * 60);
    }

    public void unmute(Bot bot, User user) {
        if (!this.hasGroupPermission(bot)) {
            return;
        }
        bot.setGroupBan(this.groupId, user.getUserId(), 0);
    }

    public void mute(Bot bot, User user, int duration) {
        if (!this.hasGroupPermission(bot)) {
            return;
        }
        bot.setGroupBan(this.groupId, user.getUserId(), duration);
    }

    public void muteForWhole(Bot bot, boolean enabled) {
        if (!this.hasGroupPermission(bot)) {
            return;
        }
        bot.setGroupWholeBan(this.groupId, enabled);
    }

    public void kick(Bot bot, User user, boolean rejectAddRequest) {
        if (!this.hasGroupPermission(bot)) {
            return;
        }
        bot.setGroupKick(this.groupId, user.getUserId(), rejectAddRequest);
    }

    public void exitGroup(Bot bot) {
        bot.setGroupLeave(this.groupId, false);
    }

    public boolean hasPermission(String permissionName) {
        return this.permissions.contains(permissionName);
    }

    public boolean hasMember(Bot bot, User user) {
        if (user == null) {
            return false;
        }
        ActionList<GroupMemberInfoResp> groupMemberList = bot.getGroupMemberList(this.groupId);
        if (groupMemberList == null) {
            return false;
        }
        List<GroupMemberInfoResp> data = groupMemberList.getData();
        for (GroupMemberInfoResp datum : data) {
            if (Objects.equals(datum.getUserId(), user.getUserId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGroupPermission(Bot bot) {
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(this.groupId, bot.getSelfId(), true);
        if (groupMemberInfo == null) {
            return false;
        }
        GroupMemberInfoResp data = groupMemberInfo.getData();
        return data.getRole().equalsIgnoreCase("admin") || data.getRole().equalsIgnoreCase("owner");
    }

    public boolean hasGroupPermission(Bot bot, User user) {
        return this.isAdmin(bot, user) || this.isOwner(bot, user);
    }

    public boolean isAdmin(Bot bot, User user) {
        if (user == null) {
            return false;
        }
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(this.groupId, user.getUserId(), true);
        if (groupMemberInfo == null) {
            return false;
        }
        GroupMemberInfoResp data = groupMemberInfo.getData();
        return data.getRole().equalsIgnoreCase("admin");
    }

    public boolean isOwner(Bot bot, User user) {
        if (user == null) {
            return false;
        }
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(this.groupId, user.getUserId(), true);
        if (groupMemberInfo == null) {
            return false;
        }
        GroupMemberInfoResp data = groupMemberInfo.getData();
        return data.getRole().equalsIgnoreCase("owner");
    }

    public boolean isMember(Bot bot, User user) {
        if (user == null) {
            return false;
        }
        ActionData<GroupMemberInfoResp> groupMemberInfo = bot.getGroupMemberInfo(this.groupId, user.getUserId(), true);
        if (groupMemberInfo == null) {
            return false;
        }
        GroupMemberInfoResp data = groupMemberInfo.getData();
        return data.getRole().equalsIgnoreCase("member");
    }

}
