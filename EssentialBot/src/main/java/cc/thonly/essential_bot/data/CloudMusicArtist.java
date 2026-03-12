package cc.thonly.essential_bot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CloudMusicArtist {
    private Long id = -1L;
    private String name = "";
    private List<String> alias = new ArrayList<>();

    public static CloudMusicArtist parse(JsonElement element) {
        if (!(element instanceof JsonObject object)) {
            return null;
        }
        CloudMusicArtist album = new CloudMusicArtist();
        album.setId(object.get("id").getAsLong());
        album.setName(object.get("name").getAsString());
        JsonElement jsonElement = object.get("alias");
        if (jsonElement instanceof JsonArray array) {
            for (JsonElement jsonEle : array) {
                album.getAlias().add(jsonEle.getAsString());
            }
        }
        return album;
    }
}
