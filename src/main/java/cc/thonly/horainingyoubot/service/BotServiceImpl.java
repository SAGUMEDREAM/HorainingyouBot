package cc.thonly.horainingyoubot.service;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BotServiceImpl {
    @Resource
    private BotContainer botContainer;

    public Optional<Bot> getBot(long botId) {
        return Optional.ofNullable(this.botContainer.robots.get(botId));
    }

}
