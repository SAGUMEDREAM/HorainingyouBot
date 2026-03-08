package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.browser.MarkdownImageFactory;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.controller.TempFileController;
import cc.thonly.horainingyoubot.util.HTTPReq;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.maimai.Maimai;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Command
public class CommandSearchSongVoice implements CommandEntrypoint {
    @Autowired
    MarkdownImageFactory markdownImageFactory;

    @Autowired
    TempFileController tempFileController;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("点歌")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            if (keyword == null) {
                                return;
                            }
                            MsgTool.reply(bot, event, "查找中...");
                            List<Song> songs = SongManager.searchSong(keyword);
                            if (songs.isEmpty()) {
                                MsgTool.reply(bot, event, "暂无结果");
                                return;
                            }
                            if (songs.size() == 1) {
                                Song song = songs.getFirst();
                                BufferedImage image = SongInfoGenerator.generate(song.id);
                                try {
                                    byte[] data = Maimai.image2Bytes(image, "png");
                                    MsgTool.reply(bot, event, MsgTool.img(data));
                                } catch (Exception e) {
                                    log.error("Error: ", e);
                                }
                                return;
                            }
                            StringBuilder result = new StringBuilder("请输入序号选择，搜索结果(%s)：\n".formatted(keyword));
                            Map<String, Song> sid2Song = new HashMap<>();
                            for (int i = 0; i < songs.size(); i++) {
                                Song song = songs.get(i);
                                sid2Song.put(i + "", song);
                                result.append("%s. %s - %s\n".formatted(i, song.info.artist, song.title));
                                if (i > 10) {
                                    break;
                                }
                            }
                            MsgTool.reply(bot, event, result.toString());
                            LinkedMessage.start(bot, event, ctx -> {
                                AnyMessageEvent next = ctx.waitNext(15);
                                if (next.getMessage().contains("./cancel")) {
                                    bot.sendMsg(event, "已取消", false);
                                    return;
                                }
                                String s = MsgTool._getText(next.getArrayMsg().getFirst());
                                Song song = sid2Song.get(s);
                                if (song == null) {
                                    bot.sendMsg(event, "已取消", false);
                                    return;
                                }
                                String api = "https://assets2.lxns.net/maimai/music/%s.mp3".formatted(song.id);
                                byte[] bytes = HTTPReq.downloadFile(api);
                                UUID audioId = this.tempFileController.saveFile(bytes);
                                try {
                                    bot.sendMsg(event, ArrayMsgUtils.builder().voice("http://127.0.0.1:9920/api/temp_file/get_voice/%s".formatted(audioId)).build(), false);
                                } catch (Exception e) {
                                    MsgTool.reply(bot, event, "获取失败");
                                    log.error("Error: ", e);
                                }
                            });
                        })
        );

    }
}