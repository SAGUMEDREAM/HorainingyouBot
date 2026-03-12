package cc.thonly.horainingyoubot.aop;

import cc.thonly.horainingyoubot.config.BotProperties;
import cc.thonly.horainingyoubot.service.BotServiceImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Optional;

//@Aspect
//@Component
public class GlobalExceptionReporter {
//    @Autowired
//    BotProperties botProperties;
//
//    @Autowired
//    BotServiceImpl botService;
//
//    @Pointcut(
//            "(" +
//                    "within(@org.springframework.stereotype.Component *) || " +
//                    "within(@org.springframework.stereotype.Service *) || " +
//                    "within(@org.springframework.stereotype.Repository *) || " +
//                    "within(@org.springframework.stereotype.Controller *) || " +
//                    "within(@org.springframework.web.bind.annotation.RestController *) || " +
//                    "within(@cc.thonly.horainingyoubot.command.Command *) || " +
//                    "this(cc.thonly.horainingyoubot.core.JPlugin)" +
//                    ")" +
//                    " && !within(cc.thonly.horainingyoubot.aop.GlobalExceptionReporter)"
//    )
//    public void springBean() {
//    }
//
//    @AfterThrowing(pointcut = "springBean()", throwing = "e")
//    public void afterThrow(Throwable e) {
//        this.report(e);
//    }
//
//    public void report(Throwable e) {
//        try {
//
//            Long botGroupId = botProperties.getBotGroupId();
//            Long selfId = botProperties.getSelfId();
//
//            if (botGroupId == null || selfId == null) return;
//            if (botGroupId == -1L || selfId == -1L) return;
//
//            Optional<Bot> botOptional = botService.getBot(selfId);
//            if (botOptional.isEmpty()) return;
//
//            Bot bot = botOptional.get();
//
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//
//            String msg = sw.toString();
//
//            bot.sendGroupMsg(
//                    botGroupId,
//                    ArrayMsgUtils.builder().text(msg).build(),
//                    false
//            );
//
//        } catch (Throwable ignored) {
//        }
//    }

}
