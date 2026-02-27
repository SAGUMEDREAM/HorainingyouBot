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

    public boolean hasPermission(String permissionName) {
        return this.permissions.contains(permissionName);
    }

}
