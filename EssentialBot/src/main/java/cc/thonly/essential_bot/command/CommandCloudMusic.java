package cc.thonly.essential_bot.command;

import cc.thonly.essential_bot.data.CloudMusicSong;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.*;

@Slf4j
@Command
public class CommandCloudMusic implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("网易云点歌")
                        .withAliasName("wyydg")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            if (keyword == null) {
                                return;
                            }
                            MsgTool.reply(bot, event, "正在搜索中");
                            OkHttpClient client = new OkHttpClient();
                            HttpUrl httpUrl = HttpUrl.parse("https://music.163.com/api/cloudsearch/pc");
                            if (httpUrl == null) {
                                return;
                            }
                            HttpUrl url = httpUrl.newBuilder()
                                    .addQueryParameter("s", keyword)
                                    .addQueryParameter("type", "1")
                                    .addQueryParameter("offset", "0")
                                    .addQueryParameter("limit", "5")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .get()
                                    .build();
                            try (Response response = client.newCall(request).execute()) {
                                ResponseBody body = response.body();
                                if (body == null) {
                                    return;
                                }
                                JsonElement jsonElement = JsonParser.parseString(body.string());
                                if (!(jsonElement instanceof JsonObject object)) {
                                    return;
                                }
                                JsonElement result = object.get("result");
                                if (!(result instanceof JsonObject resultObject)) {
                                    return;
                                }
                                JsonElement songs = resultObject.get("songs");
                                if (!(songs instanceof JsonArray songsArray)) {
                                    return;
                                }
                                List<CloudMusicSong> songList = new ArrayList<>();
                                for (JsonElement songElement : songsArray) {
                                    CloudMusicSong song = CloudMusicSong.parse(songElement);
                                    if (song == null) {
                                        continue;
                                    }
                                    songList.add(song);
                                }
                                Map<Integer, CloudMusicSong> forIndex = new LinkedHashMap<>();
                                for (int i = 0; i < songList.size(); i++) {
                                    forIndex.put(i + 1, songList.get(i));
                                }
                                LinkedMessage.start(bot, event, ctx -> {
                                    StringBuilder s = new StringBuilder();
                                    int idx = 0;
                                    for (Map.Entry<Integer, CloudMusicSong> mapEntry : forIndex.entrySet()) {
                                        Integer index = mapEntry.getKey();
                                        CloudMusicSong song = mapEntry.getValue();
                                        s.append("%s. %s - %s".formatted(index, song.getName(), song.getAlbum() != null ? song.getAlbum().getName() : "无专辑"));
                                        if (idx < forIndex.size()) {
                                            s.append("\n");
                                        }
                                        idx++;
                                    }

                                    MsgTool.reply(bot, event, ArrayMsgUtils
                                            .builder()
                                            .text(s.toString())
                                            .build()
                                    );
                                    AnyMessageEvent next = ctx.waitNext(15);
                                    List<ArrayMsg> arrayMsg = next.getArrayMsg();
                                    String string = MsgTool.toString(arrayMsg);
                                    Integer musicIndex = null;
                                    try {
                                        musicIndex = Integer.parseInt(string);
                                    } catch (Exception ignored) {
                                    }
                                    if (musicIndex == null) {
                                        return;
                                    }
                                    CloudMusicSong song = forIndex.get(musicIndex);
                                    if (song == null) {
                                        return;
                                    }
                                    List<ArrayMsg> card = ArrayMsgUtils.builder().music("163", song.getId()).build();
                                    bot.sendMsg(event, card, false);
                                });
                            } catch (Exception e) {
                                log.error("Error: ", e);
                                bot.sendMsg(event, "搜索失败，出现错误", false);
                            }
                        })
        );
    }
}