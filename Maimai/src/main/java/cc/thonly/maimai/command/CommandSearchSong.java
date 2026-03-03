package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;

import java.util.List;

@Command
public class CommandSearchSong implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("查歌")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            if (keyword == null) {
                                return;
                            }
                            List<Song> songs = SongManager.searchSong(keyword);

                        })
        );

    }
}