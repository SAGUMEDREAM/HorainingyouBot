package cc.thonly.horainingyoubot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    private Long selfId = -1L;
    private Long ownerId = -1L;
}