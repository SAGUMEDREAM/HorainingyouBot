package cc.thonly.horainingyoubot.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class BotConfigInitializer {
    private static final Path BOT_CONFIG = Path.of("./config/bot.yml");

    @PostConstruct
    public void init() throws IOException {
        if (Files.exists(BOT_CONFIG)) {
            return;
        }

        Files.createDirectories(BOT_CONFIG.getParent());

        String defaultConfig = """
                bot:
                  self_id: 2769345831
                  owner_id: 807131829
                """;

        Files.writeString(BOT_CONFIG, defaultConfig);

        System.out.println("已自动生成 config/bot.yml");
    }
}
