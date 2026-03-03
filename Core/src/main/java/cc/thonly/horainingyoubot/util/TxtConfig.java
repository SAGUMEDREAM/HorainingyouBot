package cc.thonly.horainingyoubot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

@Slf4j
public class TxtConfig {
    public static void read(Path path, Consumer<String> reader) {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                reader.accept(line);
            }
        } catch (IOException e) {
            log.error("Can't read file {}", path, e);
        }
    }

    public static void createIfNoExist(Path path) {
        try {
            if (!Files.exists(path)) {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                Files.createFile(path);
            }
        } catch (IOException e) {
            log.error("Can't create file {}", path, e);
        }
    }

    public static void writeLine(Path path, String line) {
        try (BufferedWriter bw = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.newLine();
            bw.write(line);
            bw.newLine(); // 换行
        } catch (IOException e) {
            log.error("Can't write line to file {}", path, e);
        }
    }

    public static void writeLines(Path path, Consumer<BufferedWriter> lineConsumer) {
        try (BufferedWriter bw = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            lineConsumer.accept(bw);
        } catch (IOException e) {
            log.error("Can't write lines to file {}", path, e);
        }
    }
}
