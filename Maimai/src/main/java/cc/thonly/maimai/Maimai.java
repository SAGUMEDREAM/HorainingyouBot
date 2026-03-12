package cc.thonly.maimai;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.maimai.command.*;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class Maimai implements JPlugin {

    @Autowired
    Commands commands;

    @Override
    public void onInitialize() {
        if (ConfigRefs.check()) {
            ConfigRefs.init();
        }
        if (!SongManager.isInitialized()) {
            Thread.startVirtualThread(SongManager::init);
        }
        this.registerCommands(this.commands);
    }

    @Autowired
    CommandB50 commandB50;
    @Autowired
    CommandSearchSong commandSearchSong;
    @Autowired
    CommandSongPlayData commandSongPlayData;
    @Autowired
    CommandCompletionTable commandCompletionTable;
    @Autowired
    CommandMaiAlias commandMaiAlias;
    @Autowired
    CommandMaiFriend commandMaiFriend;
    @Autowired
    CommandMaiAwake commandMaiAwake;

    @Override
    public void registerCommands(Commands commands) {
        commands.registerCommand(this.commandB50);
        commands.registerCommand(this.commandSearchSong);
        commands.registerCommand(this.commandSongPlayData);
        commands.registerCommand(this.commandCompletionTable);
        commands.registerCommand(this.commandMaiAlias);
        commands.registerCommand(this.commandMaiFriend);
        commands.registerCommand(this.commandMaiAwake);
    }

    public static byte[] image2Bytes(BufferedImage image, String format) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    @Override
    public String getPluginId() {
        return "maimai";
    }
}
