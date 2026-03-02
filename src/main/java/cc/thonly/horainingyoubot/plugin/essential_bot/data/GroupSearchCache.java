package cc.thonly.horainingyoubot.plugin.essential_bot.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class GroupSearchCache {
    Long timestamp;
    Data data;

    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    public static class Data {
        List<Item> data;
    }

    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    public static class Item {
        String city;
        @JsonProperty("group_name")
        String groupName;
        @JsonProperty("event_id")
        String eventName;
        @JsonProperty("group_id")
        String groupId;
        String type;
    }
}
