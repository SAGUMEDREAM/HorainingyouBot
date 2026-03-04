package cc.thonly.horainingyoubot.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioTool {

    public static byte[] convertToMp3(byte[] audioData) throws IOException, InterruptedException {
        Path tempWav = Files.createTempFile("audio", ".wav");
        Files.write(tempWav, audioData);

        Path tempMp3 = Files.createTempFile("audio", ".mp3");

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-i",
                tempWav.toAbsolutePath().toString(),
                tempMp3.toAbsolutePath().toString()
        );
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg 转换失败，退出码：" + exitCode);
        }

        byte[] mp3Bytes = Files.readAllBytes(tempMp3);

        Files.deleteIfExists(tempWav);
        Files.deleteIfExists(tempMp3);

        return mp3Bytes;
    }

    public static byte[] convertToWav(byte[] audioData) throws IOException, InterruptedException {
        Path tempInput = Files.createTempFile("audio_input", ".tmp");
        Files.write(tempInput, audioData);

        Path tempOutput = Files.createTempFile("audio_output", ".wav");

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-i",
                tempInput.toAbsolutePath().toString(),
                tempOutput.toAbsolutePath().toString()
        );
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            Files.deleteIfExists(tempInput);
            Files.deleteIfExists(tempOutput);
            throw new RuntimeException("FFmpeg 转 WAV 失败，退出码：" + exitCode);
        }

        byte[] wavBytes = Files.readAllBytes(tempOutput);

        Files.deleteIfExists(tempInput);
        Files.deleteIfExists(tempOutput);

        return wavBytes;
    }
}