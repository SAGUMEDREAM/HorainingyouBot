package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.maimai.Maimai;
import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.api.SongPlayDataGenerator;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

@Slf4j
@Command
public class CommandSongPlayData implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("游玩数据")
                        .withArguments("#{song_name|song_id}")
                        .withExecutor((bot, event, args) -> {
                            Long userId = event.getUserId();
                            String input = args.getString("song_name|song_id");
                            int songId = -1;
                            try {
                                @SuppressWarnings("UnnecessaryLocalVariable")
                                int i = Integer.parseInt(input);
                                songId = i;
                            } catch (Exception e) {
                                List<Song> songs = SongManager.searchSong(input);
                                if (songs.isEmpty()) {
                                    MsgTool.reply(bot, event, "歌曲无搜索结果");
                                    return;
                                }
                                Song song = songs.getFirst();
                                songId = song.id;
                            }
                            BufferedImage image = SongPlayDataGenerator.generate(userId, songId);
                            try {
                                byte[] data = Maimai.image2Bytes(image, "png");
                                MsgTool.reply(bot, event, MsgTool.img(data));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        })
                        .up()
                        .addNode("other", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user");
                            if (userOptional.isEmpty()) {
                                return;
                            }
                            User user = userOptional.get();
                            Long userId = user.getUserId();
                            String input = args.getString("song_name|song_id");
                            int songId = -1;
                            try {
                                @SuppressWarnings("UnnecessaryLocalVariable")
                                int i = Integer.parseInt(input);
                                songId = i;
                            } catch (Exception e) {
                                List<Song> songs = SongManager.searchSong(input);
                                if (songs.isEmpty()) {
                                    MsgTool.reply(bot, event, "歌曲无搜索结果");
                                    return;
                                }
                                Song song = songs.getFirst();
                                songId = song.id;
                            }
                            BufferedImage image = SongPlayDataGenerator.generate(userId, songId);
                            try {
                                byte[] data = Maimai.image2Bytes(image, "png");
                                MsgTool.reply(bot, event, MsgTool.img(data));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        })
                        .withArguments("#{user} #{song_name|song_id}")
                        .up()
        );

    }
}