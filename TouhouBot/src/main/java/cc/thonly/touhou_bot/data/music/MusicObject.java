package cc.thonly.touhou_bot.data.music;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MusicObject {
    private String name;
    private String from;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
