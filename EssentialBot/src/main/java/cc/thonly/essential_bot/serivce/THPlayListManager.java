package cc.thonly.essential_bot.serivce;

import cc.thonly.essential_bot.data.music.GameObject;
import cc.thonly.essential_bot.data.music.MusicObject;
import cc.thonly.essential_bot.data.music.THPlayListObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class THPlayListManager {

    private THPlayListObject assets;

    public THPlayListManager() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            URL resource = THPlayListManager.class.getClassLoader().getResource("static/assets/thplaylist.json");
            if (resource == null) {
                throw new IOException("资源文件 thplaylist.json 未找到");
            }

            File file = Paths.get(resource.toURI()).toFile();
            this.assets = objectMapper.readValue(file, THPlayListObject.class);

            for (GameObject game : assets.getData()) {
                for (MusicObject music : game.getPlaylist()) {
                    music.setFrom(game.getName());
                }
            }
        } catch (Exception e) {
            log.error("Can't load file: ", e);
        }
    }

    /**
     * 随机获取一个音乐对象，确保 playlist 不为空
     */
    public MusicObject random() {
        if (this.assets.getData().isEmpty()) {
            System.err.println("音乐库为空，无法随机获取音乐");
            return null;
        }

        Random random = new Random();
        GameObject game;
        do {
            game = this.assets.getData().get(random.nextInt(this.assets.getData().size()));
        } while (game.getPlaylist().isEmpty());

        return game.getPlaylist().get(random.nextInt(game.getPlaylist().size()));
    }

    /**
     * 根据游戏名称获取 GameObject
     */
    public GameObject getGame(String keyword) {
        for (GameObject game : this.assets.getData()) {
            if (game.getName().contains(keyword)) {
                return game;
            }
        }
        return null;
    }

    /**
     * 根据关键词查找所有匹配的 MusicObject
     */
    public List<MusicObject> find(String keyword) {
        List<MusicObject> result = new ArrayList<>();
        for (GameObject game : this.assets.getData()) {
            for (MusicObject music : game.getPlaylist()) {
                if (music.getName().contains(keyword)) {
                    result.add(music);
                }
            }
        }
        return result;
    }

}