package cc.thonly.touhou_bot.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class THSearchResult {
    String status;
    String name;
    String area;
    String time;
    @JsonProperty("group_id")
    String groupId;
    Long timestamp;
}
