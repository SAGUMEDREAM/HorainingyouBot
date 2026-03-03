package cc.thonly.horainingyoubot;

import cc.thonly.horainingyoubot.core.CorePlugin;
import cc.thonly.horainingyoubot.core.SpringContextHolder;
import cc.thonly.horainingyoubot.util.DeferTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@EnableJpaRepositories(basePackages = "cc.thonly")
@SpringBootApplication(scanBasePackages = "cc.thonly")
@EntityScan(basePackages = "cc.thonly")
public class HorainingyouBotApplication {

    public static void main(String[] args) {
        earlySetup();
        ConfigurableApplicationContext run = SpringApplication.run(HorainingyouBotApplication.class, args);
        SpringContextHolder.setContext(run);
        CorePlugin.onLoad();
    }

    public static void earlySetup() {

    }
}
