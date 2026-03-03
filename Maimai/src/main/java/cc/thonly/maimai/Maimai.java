package cc.thonly.maimai;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.maimai.command.CommandB50;
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
        Thread.startVirtualThread(SongManager::init);
        this.registerCommands(this.commands);
    }

    @Autowired
    CommandB50 commandB50;

    @Override
    public void registerCommands(Commands commands) {
        commands.registerCommand(this.commandB50);
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
