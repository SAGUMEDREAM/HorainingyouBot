package cc.thonly.otomad_helper;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.otomad_helper.command.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;

@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class OtomadHelper implements JPlugin {
    private static final String[] DEPENDENCIES = {
            "numpy", "scipy", "PyWavelets", "soundfile", "pyworld"
    };

    @Autowired
    Commands commands;

    boolean hasPython = false;

    @Override
    public void onInitialize() {
        this.hasPython = this.checkPython();
        this.checkPythonDep();
        this.ifNotExistRelease("./static/script/BpmDetector.py", "./script/BpmDetector.py");
        this.ifNotExistRelease("./static/script/PitchCorrection4Mirai.py", "./PitchCorrection4Mirai.py");
        this.registerCommands(this.commands);
    }

    @Autowired
    CommandRandomTutorial commandRandomTutorial;
    @Autowired
    CommandMidiShow commandMidiShow;
    @Autowired
    CommandOtmWiki commandOtmWiki;
    @Autowired
    CommandNewtone commandNewtone;
    @Autowired
    CommandBpmDetector commandBpmDetector;
    @Autowired
    CommandBilibili commandBilibili;
    @Autowired
    CommandCrystalBall commandCrystalBall;
    @Autowired
    CommandSpherization commandSpherization;
    @Autowired
    CommandDespherization commandDespherization;
    @Autowired
    CommandIMSoHappy commandIMSoHappy;
    @Autowired
    CommandMaiFriend commandMaiFriend;
    @Autowired
    CommandMaiAwake commandMaiAwake;

    @Override
    public void registerCommands(Commands commands) {
        commands.registerCommand(this.commandRandomTutorial);
        commands.registerCommand(this.commandMidiShow);
        commands.registerCommand(this.commandOtmWiki);
        commands.registerCommand(this.commandBilibili);
        commands.registerCommand(this.commandCrystalBall);
        commands.registerCommand(this.commandSpherization);
        commands.registerCommand(this.commandDespherization);
        commands.registerCommand(this.commandIMSoHappy);
        commands.registerCommand(this.commandMaiFriend);
        commands.registerCommand(this.commandMaiAwake);
        if (this.hasPython) {
            commands.registerCommand(this.commandNewtone);
            commands.registerCommand(this.commandBpmDetector);
        }
    }

    boolean checkPython() {
        try {
            Process process = new ProcessBuilder("python", "--version")
                    .redirectErrorStream(true)
                    .start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();
            process.waitFor();
            if (version != null && version.startsWith("Python")) {
                log.info("Python detected: {}", version);
                return true;
            }
        } catch (IOException | InterruptedException e) {
            try {
                Process process = new ProcessBuilder("python3", "--version")
                        .redirectErrorStream(true)
                        .start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String version = reader.readLine();
                process.waitFor();
                if (version != null && version.startsWith("Python")) {
                    log.info("Python3 detected: {}", version);
                    return true;
                }
            } catch (IOException | InterruptedException ex) {
                // ignore
            }
        }
        log.warn("Python not detected. Please install Python first.");
        return false;
    }

    void checkPythonDep() {
        String pythonCmd = "python";
        if (!this.checkPython()) {
            log.warn("Python not found, skipping dependency check.");
            return;
        }

        for (String dep : DEPENDENCIES) {
            try {
                Process showProc = new ProcessBuilder(pythonCmd, "-m", "pip", "show", dep)
                        .redirectErrorStream(true)
                        .start();

                // Consume output to avoid pipe blocking
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(showProc.getInputStream()))) {
                    while (reader.readLine() != null) {
                        // discard output
                    }
                }

                int code = showProc.waitFor();
                if (code != 0) {
                    log.info("Dependency {} not found. Installing...", dep);
                    Process installProc = new ProcessBuilder(pythonCmd, "-m", "pip", "install", dep)
                            .inheritIO()
                            .start();
                    installProc.waitFor();
                    log.info("Dependency {} installed successfully.", dep);
                } else {
                    log.info("Dependency {} is already installed.", dep);
                }
            } catch (IOException | InterruptedException e) {
                log.error("Failed to install dependency {}: {}", dep, e.getMessage());
            }
        }
    }

    public static boolean isAudioBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 4) return false;

        // WAV 文件头 "RIFF"
        if (bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F') return true;

        // MP3 文件头 0xFF 0xFB 或 0xFF 0xF3
        if ((bytes[0] & 0xFF) == 0xFF && ((bytes[1] & 0xE0) == 0xE0)) return true;

        // OGG 文件头 "OggS"
        if (bytes[0] == 'O' && bytes[1] == 'g' && bytes[2] == 'g' && bytes[3] == 'S') return true;

        // FLAC 文件头 "fLaC"
        if (bytes[0] == 'f' && bytes[1] == 'L' && bytes[2] == 'a' && bytes[3] == 'C') return true;

        return false;
    }

    public static boolean isAudioFile(File file) {
        try {
            String mimeType = Files.probeContentType(file.toPath());
            return mimeType != null && mimeType.startsWith("audio");
        } catch (IOException e) {
            return false;
        }
    }

    public void ifNotExistRelease(String resourcePath, String outputPath) {
        try {
            File outFile = new File(outputPath);
            if (outFile.exists()) return;

            // 注意这里使用 / 开头
            InputStream resourceAsStream = OtomadHelper.class.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath);
            if (resourceAsStream == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }

            File parentDir = outFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();

            try (OutputStream os = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = resourceAsStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            }

            resourceAsStream.close();

        } catch (Exception e) {
            log.error("Error:", e);
            throw new RuntimeException("Failed to release resource " + resourcePath + " to " + outputPath, e);
        }
    }

    @Override
    public String getPluginId() {
        return "otomad_helper";
    }
}
