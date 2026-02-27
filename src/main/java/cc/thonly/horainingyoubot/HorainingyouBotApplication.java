package cc.thonly.horainingyoubot;

import cc.thonly.horainingyoubot.core.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "cc.thonly.horainingyoubot")
@Slf4j
public class HorainingyouBotApplication {

    public static void main(String[] args) {
        earlySetup();
        ConfigurableApplicationContext run = SpringApplication.run(HorainingyouBotApplication.class, args);
        SpringContextHolder.setContext(run);
    }

    public static void earlySetup() {

    }

}
