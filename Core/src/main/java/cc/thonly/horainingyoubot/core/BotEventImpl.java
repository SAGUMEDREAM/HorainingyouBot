package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.config.BotProperties;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.CoreEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Component
public class BotEventImpl extends CoreEvent {
    WebSocketSession session;

    @Autowired
    BotProperties botProperties;

    @Override
    public void online(Bot bot) {
        super.online(bot);
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info("链接的机器人ID：{} -- 链接时间：{}", bot.getSelfId(), time);
        Long botGroupId = this.botProperties.getBotGroupId();
        if (!Objects.equals(botGroupId,-1L)) {
            bot.sendGroupMsg(botGroupId, MsgTool.simpText("我上线了哦！"), false);
        }
    }

    @Override
    public void offline(long account) {
        try {
            this.session.close();
        } catch (IOException e) {
            log.warn("机器人离线", e);
        }
        log.info("机器人 {} 链接已断开", account);
        super.offline(account);
    }

    @Override
    public boolean session(WebSocketSession session) {
        this.session = session;
        log.debug("---------------------------------------------------");
        log.debug("Attributes:{}", session.getAttributes());
        log.debug("Headers:{}", session.getHandshakeHeaders());
        log.debug("AcceptedProtocol:{}", session.getAcceptedProtocol());
        log.debug("LocalAddress:{}", session.getLocalAddress());
        log.debug("Extensions:{}", session.getExtensions());
        log.debug("---------------------------------------------------");
        return super.session(session);
    }

    public WebSocketSession getSession() {
        return session;
    }
}
