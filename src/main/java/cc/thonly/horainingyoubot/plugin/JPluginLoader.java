package cc.thonly.horainingyoubot.plugin;

import cc.thonly.horainingyoubot.core.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class JPluginLoader {
    private final Map<String, JPlugin> id2plugin = new LinkedHashMap<>();

    public void loadPlugins() {
        this.id2plugin.clear();
        List<String> lines = this.loadCfg();
        for (String line : lines) {
            try {
                Class<?> clz = Class.forName(line);
                if (!JPlugin.class.isAssignableFrom(clz)) {
                    log.error("{} is not a JPlugin", clz);
                    continue;
                }
                Constructor<?>[] declaredConstructors = clz.getDeclaredConstructors();
                for (Constructor<?> constructor : declaredConstructors) {
                    Object instance = constructor.newInstance();
                    AutowireCapableBeanFactory factory =
                            SpringContextHolder.getBeanFactory();
                    factory.autowireBean(instance);
                    instance = factory.initializeBean(instance, clz.getName());

                    JPlugin jPlugin = (JPlugin) instance;
                    this.id2plugin.put(jPlugin.getPluginId(), jPlugin);
                    break;
                }
            } catch (Exception e) {
                log.error("Can't load plugin: {}", line, e);
            }
        }
        this.id2plugin.forEach((id, jPlugin) -> {
            jPlugin.onInitialize();
        });
    }

    public void unloadPlugins() {
        AutowireCapableBeanFactory factory =
                SpringContextHolder.getBeanFactory();

        for (JPlugin plugin : id2plugin.values()) {
            try {
                factory.destroyBean(plugin);
            } catch (Exception e) {
                log.error("Destroy plugin error: {}", plugin.getPluginId(), e);
            }
        }

        id2plugin.clear();
    }

    public Set<Map.Entry<String, JPlugin>> entries() {
        return this.id2plugin.entrySet();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<String> loadCfg() {
        List<String> list = new ArrayList<>();
        File file = new File("./j-plugins.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("Can't create file j-plugins.txt");
            }
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                list.add(line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
