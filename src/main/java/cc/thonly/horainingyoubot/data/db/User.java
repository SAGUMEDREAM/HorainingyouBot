package cc.thonly.horainingyoubot.data.db;

import cc.thonly.horainingyoubot.converter.CustomDataConverter;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    private Long userId = -1L;
    private String username = "";
    private String userAvatarUrl = "";
    private Boolean eula = false;
    private Boolean banned = false;
    private Integer permissionLevel = 0;
    private List<String> permissions = new ArrayList<>();

    @Convert(converter = CustomDataConverter.class)
    private CustomData customData = new CustomData();

    public void setPermissionLevel(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public void setPermissionLevel(PermissionLevel level) {
        this.permissionLevel = level.level();
    }

    public boolean hasPermissionLevel(PermissionLevel level) {
        return level.matches(this);
    }

    public boolean hasPermissionLevel(Integer levelValue) {
        return this.permissionLevel >= levelValue;
    }

    public boolean hasAcceptedEula() {
        return this.permissionLevel >= 2 || this.eula;
    }

    public Boolean getEula() {
        return this.hasAcceptedEula();
    }

}
