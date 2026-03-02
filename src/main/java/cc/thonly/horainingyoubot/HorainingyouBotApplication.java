package cc.thonly.horainingyoubot;

import cc.thonly.horainingyoubot.core.SpringContextHolder;
import cc.thonly.horainingyoubot.util.DeferTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = "cc.thonly.horainingyoubot")
@Slf4j
public class HorainingyouBotApplication {
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static int START_ID = 0;

    public static void main(String[] args) {
        earlySetup();
        ConfigurableApplicationContext run = SpringApplication.run(HorainingyouBotApplication.class, args);
        SpringContextHolder.setContext(run);
        scheduler.scheduleAtFixedRate(() -> {
            DeferTask.tick(START_ID);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static void earlySetup() {

    }

}
