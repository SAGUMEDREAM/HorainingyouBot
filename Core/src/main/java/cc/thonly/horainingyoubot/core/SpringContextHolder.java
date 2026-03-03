package cc.thonly.horainingyoubot.core;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) {
        context = ctx;
    }

    public static void setContext(ApplicationContext newContext) {
        if (!Objects.equals(newContext, context)) {
            context = newContext;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static AutowireCapableBeanFactory getBeanFactory() {
        return context.getAutowireCapableBeanFactory();
    }
}
