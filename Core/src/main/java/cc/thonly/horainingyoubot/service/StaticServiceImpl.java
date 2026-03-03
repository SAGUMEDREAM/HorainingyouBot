package cc.thonly.horainingyoubot.service;

import cc.thonly.horainingyoubot.core.CorePlugin;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URL;
import java.io.File;

@Slf4j
@Service
public class StaticServiceImpl {
    private static final Class<?> DEFAULT_PARENT = CorePlugin.class;

    public Wrapper getDefault() {
        return Wrapper.OBJECTS.computeIfAbsent(DEFAULT_PARENT, (c) -> new Wrapper(DEFAULT_PARENT));
    }

    public Wrapper get(Class<?> target) {
        return Wrapper.OBJECTS.computeIfAbsent(target, (c) -> new Wrapper(target));
    }

    public static class Wrapper {
        private static final Map<Class<?>, Wrapper> OBJECTS = new HashMap<>();
        private final Class<?> parent;

        public Wrapper(Class<?> parent) {
            this.parent = parent;
        }

        public List<String> getFilenames(String path) {
            List<String> filenames = new ArrayList<>();
            try {
                URL dirURL = this.parent.getResource("/static/" + path);
                if (dirURL != null && dirURL.getProtocol().equals("file")) {
                    File folder = new File(dirURL.toURI());
                    for (File file : Objects.requireNonNull(folder.listFiles())) {
                        filenames.add(file.getName());
                    }
                } else if (dirURL != null && dirURL.getProtocol().equals("jar")) {
                    String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip "file:" and everything after "!"
                    try (JarFile jar = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jar.entries();
                        String prefix = "static/" + path + "/";
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            if (entry.getName().startsWith(prefix) && !entry.isDirectory()) {
                                filenames.add(entry.getName().substring(prefix.length()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error: ", e);
            }
            return filenames;
        }


        public String getAsText(String path) {
            try (InputStream in = this.parent.getResourceAsStream("/static/" + path); BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (Exception e) {
                log.error("Error: ", e);
                return null;
            }
        }

        public JsonElement getAsJsonElement(String path) {
            String text = getAsText(path);
            if (text == null) return null;
            return JsonParser.parseString(text);
        }

        public byte[] getAsByteArray(String path) {
            try (InputStream in = this.parent.getResourceAsStream("/static/" + path)) {
                if (in == null) return null;
                return in.readAllBytes();
            } catch (Exception e) {
                log.error("Error: ", e);
                return null;
            }
        }
    }
}
