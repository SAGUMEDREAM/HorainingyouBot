package cc.thonly.horainingyoubot.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class DataManagerImpl {

    private static final Gson GSON = new Gson();

    public List<String> getFileNames(String filePath) {
        Path dir = Path.of("./data/%s".formatted(filePath));

        if (!Files.isDirectory(dir)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            log.error("Failed to list files in {}", dir, e);
            return List.of();
        }
    }

    public byte[] get(String filePath) {
        Path path = Path.of("./data/%s".formatted(filePath));
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Can't read file {}:", path, e);
            return null;
        }
    }

    public InputStream getStream(String filePath) {
        Path path = Path.of("./data/%s".formatted(filePath));
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            log.error("Can't read file {}:", path, e);
            return null;
        }
    }

    private Path resolvePath(String filePath) {
        Path root = Path.of("./data").toAbsolutePath().normalize();
        Path target = root.resolve(filePath).normalize();

        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("非法路径: " + filePath);
        }
        return target;
    }

    public void save(String filePath, byte[] data) {
        Path path = this.resolvePath(filePath);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, data);
        } catch (IOException e) {
            log.error("Can't save file {}:", path, e);
        }
    }

    public String getText(String filePath) {
        Path path = resolvePath(filePath);
        if (!Files.exists(path)) return null;

        try {
            return Files.readString(path);
        } catch (IOException e) {
            log.error("Can't read text {}:", path, e);
            return null;
        }
    }

    public void saveText(String filePath, String text) {
        this.save(filePath, text.getBytes(StandardCharsets.UTF_8));
    }

    public <T> T getJson(String filePath, Class<T> type) {
        String text = getText(filePath);
        if (text == null) return null;
        return GSON.fromJson(text, type);
    }

    public long size(String filePath) {
        try {
            return Files.size(resolvePath(filePath));
        } catch (IOException e) {
            return -1;
        }
    }

    public boolean delete(String filePath) {
        try {
            return Files.deleteIfExists(resolvePath(filePath));
        } catch (IOException e) {
            log.error("Can't delete file {}:", filePath, e);
            return false;
        }
    }

    public boolean exists(String filePath) {
        return Files.exists(resolvePath(filePath));
    }
}

