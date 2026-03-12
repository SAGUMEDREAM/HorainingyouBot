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
public class CloudMusicAlbum {
    private Long id = -1L;
    private String name = "";
    private String pic = "";
    private List<CloudMusicSong> songs = new ArrayList<>();

    public static CloudMusicAlbum parse(JsonElement element) {
        if (!(element instanceof JsonObject object)) {
            return null;
        }
        CloudMusicAlbum album = new CloudMusicAlbum();
        album.setId(object.get("id").getAsLong());
        album.setName(object.get("name").getAsString());
        album.setPic(object.get("picUrl").getAsString());
        return album;
    }

}
