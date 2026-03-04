package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.horainingyoubot.util.Mth;
import cc.thonly.maimai.Maimai;
import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.List;

@Slf4j
@Command
public class CommandMaiRandom implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机歌曲")
                        .withExecutor((bot, event, args) -> {
                            List<Song> songs = SongManager.getSongs();
                            Song song = Mth.getRandomElement(songs);
                            if (song == null) {
                                MsgTool.reply(bot, event, "获取失败");
                                return;
                            }
                            MsgTool.reply(bot, event, "获取中...");
                            BufferedImage image = SongInfoGenerator.generate(song.id);
                            try {
                                byte[] data = Maimai.image2Bytes(image, "png");
                                MsgTool.reply(bot, event, MsgTool.img(data));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        })
        );

    }
}