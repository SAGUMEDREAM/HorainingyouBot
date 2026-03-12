package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import cc.thonly.horainingyoubot.util.MsgTool;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Command
public class CommandCreateOtomad implements CommandEntrypoint {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final boolean ENABLED = false;
    private static boolean WORKING = false;

    @Override
    public void registerCommand(Commands commands) {
        try {
            List<Path> objects = List.of(
                    Path.of("./temp"),
                    Path.of("./otm-temp"),
                    Path.of("./result")
            );
            for (Path path : objects) {
                if (!Files.exists(path)) {
                    Files.createDirectory(path);
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        commands.registerCommand(
                CommandNode.createRoot("帮我做音mad")
                        .withAliasName("帮我做音MAD")
                        .withAliasName("帮我做音骂")
                        .withExecutor((bot, event, args) -> {
                            if (!ENABLED) {
                                MsgTool.reply(bot, event, "不！");
                                return;
                            }
                            if (WORKING) {
                                MsgTool.reply(bot, event, "另一个创建工作在运行中，请等候完成后重试");
                                return;
                            }

                            WORKING = true;

                            EXECUTOR.submit(() -> {

                                LinkedMessage.start(bot, event, ctx -> {

                                    LinkedMessage.FileInfo midiFileInfo =
                                            LinkedMessage.getNextFile("midi", "sy.mid", bot, event, ctx);
                                    if (midiFileInfo == null || midiFileInfo.fileBytes == null) {
                                        WORKING = false;
                                        return;
                                    }

                                    LinkedMessage.FileInfo materialsFileInfo =
                                            LinkedMessage.getNextFile("音频或视频素材", "60.mp4", bot, event, ctx);
                                    if (materialsFileInfo == null || materialsFileInfo.fileBytes == null) {
                                        WORKING = false;
                                        return;
                                    }

                                    Path tempDir = Path.of("./temp");
                                    Path resultDir = Path.of("./result");

                                    try {

                                        if (!Files.exists(tempDir)) {
                                            Files.createDirectories(tempDir);
                                        }

                                        Path midiPath = tempDir.resolve(midiFileInfo.originalName);
                                        Path srcPath = tempDir.resolve(materialsFileInfo.originalName);
                                        Path resultPath = resultDir.resolve("otm.mp4");

                                        Files.write(midiPath, midiFileInfo.fileBytes);
                                        Files.write(srcPath, materialsFileInfo.fileBytes);

                                        ProcessBuilder pb = new ProcessBuilder(
                                                "cmd", "/c",
                                                "python script/mel.py --src temp/60.mp4 --midi temp/sy.mid --out result/otm.mp4"
                                        );

                                        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                                        pb.redirectErrorStream(true);

                                        Process process = pb.start();


                                        bot.sendMsg(event, "正在工作中...", false);

                                        int code = process.waitFor();

                                        if (code != 0) {
                                            bot.sendMsg(event,
                                                    "制作失败: Python脚本返回错误代码 " + code,
                                                    false);
                                            return;
                                        }

                                        if (!Files.exists(resultPath)) {
                                            bot.sendMsg(event,
                                                    "制作失败: 输出文件不存在",
                                                    false);
                                            return;
                                        }

                                        String fileName = resultPath.getFileName().toString();

                                        if (event.getGroupId() == null) {
                                            bot.uploadPrivateFile(
                                                    event.getUserId(),
                                                    resultPath.toAbsolutePath().toString(),
                                                    fileName
                                            );
                                        } else {
                                            bot.uploadGroupFile(
                                                    event.getGroupId(),
                                                    resultPath.toAbsolutePath().toString(),
                                                    fileName
                                            );
                                        }

                                        Files.deleteIfExists(midiPath);
                                        Files.deleteIfExists(srcPath);
                                        Files.deleteIfExists(resultPath);

                                    } catch (Exception e) {
                                        log.error("制作音MAD失败", e);
                                        MsgTool.reply(bot, event, "制作音MAD时发生错误");
                                    } finally {
                                        WORKING = false;
                                    }

                                });

                            });

                        })
        );
    }
}