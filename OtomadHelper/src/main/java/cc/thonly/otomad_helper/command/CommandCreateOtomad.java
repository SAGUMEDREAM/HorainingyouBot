package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.core.Bot;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Command
public class CommandCreateOtomad implements CommandEntrypoint {

    private static boolean WORKING = false;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("帮我做音mad")
                        .withAliasName("帮我做音MAD")
                        .withAliasName("帮我做音骂")
                        .withExecutor((bot, event, args) -> {

                            LinkedMessage.start(bot, event, ctx -> {

                                if (WORKING) {
                                    MsgTool.reply(bot, event, "另一个创建工作在运行中，请等候完成后重试");
                                    return;
                                }

                                WORKING = true;

                                LinkedMessage.FileInfo midiFileInfo =
                                        LinkedMessage.getNextFile("midi", bot, event, ctx);
                                if (midiFileInfo == null || midiFileInfo.fileBytes == null) {
                                    WORKING = false;
                                    return;
                                }

                                LinkedMessage.FileInfo materialsFileInfo =
                                        LinkedMessage.getNextFile("音频或视频素材", bot, event, ctx);
                                if (materialsFileInfo == null || materialsFileInfo.fileBytes == null) {
                                    WORKING = false;
                                    return;
                                }

                                Path tempDir = Path.of("./temp-otomad-creator");

                                try {

                                    if (!Files.exists(tempDir)) {
                                        Files.createDirectories(tempDir);
                                    }

                                    Path midiPath = tempDir.resolve(midiFileInfo.originalName);
                                    Path srcPath = tempDir.resolve(materialsFileInfo.originalName);
                                    Path resultPath = tempDir.resolve("result.mp4");

                                    Files.write(midiPath, midiFileInfo.fileBytes);
                                    Files.write(srcPath, materialsFileInfo.fileBytes);

                                    String pyPath = new File("./script/mel.py").getAbsolutePath();

                                    ProcessBuilder pb = new ProcessBuilder(
                                            "python",
                                            pyPath,
                                            "-midi", midiPath.toAbsolutePath().toString(),
                                            "--src", srcPath.toAbsolutePath().toString(),
                                            "--out", resultPath.toAbsolutePath().toString()
                                    );

                                    pb.redirectErrorStream(true);
                                    pb.inheritIO();

                                    Process process = pb.start();

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

                        })
        );
    }
}