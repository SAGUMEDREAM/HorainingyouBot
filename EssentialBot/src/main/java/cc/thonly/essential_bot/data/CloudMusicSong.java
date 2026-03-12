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
public class CloudMusicSong {
    private Long id = -1L;
    private String name = "";
    private List<String> alias = new ArrayList<>();
    private List<CloudMusicArtist> artists = new ArrayList<>();
    private CloudMusicAlbum album = new CloudMusicAlbum();
    private Long publishTime;

    public static CloudMusicSong parse(JsonElement element) {
        if (!(element instanceof JsonObject object)) {
            return null;
        }
        CloudMusicSong song = new CloudMusicSong();
        song.setName(object.get("name").getAsString());
        song.setId(object.get("id").getAsLong());
        song.setPublishTime(object.get("publishTime").getAsLong());
        JsonElement alia = object.get("alia");
        if (alia instanceof JsonArray array) {
            for (JsonElement jsonElement : array) {
                song.getAlias().add(jsonElement.getAsString());
            }
        }
        JsonElement al = object.get("al");
        CloudMusicAlbum album = CloudMusicAlbum.parse(al);
        if (album!= null) {
            song.setAlbum(album);
        }
        JsonElement ars = object.get("ar");
        if (ars instanceof JsonArray array) {
            for (JsonElement jsonElement : array) {
                CloudMusicArtist artist = CloudMusicArtist.parse(jsonElement);
                if (artist!= null) {
                    song.getArtists().add(artist);
                }
            }
        }
        return song;
    }
}