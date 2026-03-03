package cc.thonly.essential_bot.data.music;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GameObject {
    private String name;
    private List<MusicObject> playlist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MusicObject> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<MusicObject> playlist) {
        this.playlist = playlist;
    }
}
