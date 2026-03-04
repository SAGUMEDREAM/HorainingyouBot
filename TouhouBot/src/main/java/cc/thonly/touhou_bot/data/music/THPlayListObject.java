package cc.thonly.touhou_bot.data.music;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class THPlayListObject {
    private String header;
    private List<GameObject> data;

}
