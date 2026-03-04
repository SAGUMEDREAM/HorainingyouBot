package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.util.*;
import cc.thonly.horainingyoubot.util.AudioTool;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Command
public class CommandNewtone implements CommandEntrypoint {

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("帮我修音")
                        .withAliasName("修音")
                        .withExecutor((bot, event, args) -> {
                            LinkedMessage.start(bot, event, ctx -> {
                                MsgTool.reply(bot, event, "请发送待处理文件");
                                AnyMessageEvent next = ctx.waitNext(30);
                                List<ArrayMsg> arrayMsg = next.getArrayMsg();
                                ArrayMsg first = arrayMsg.getFirst();
                                if (!(first.getType() == MsgTypeEnum.unknown)) return;

                                JsonNode data = first.getData();
                                String fileId = data.get("file_id").asText();
                                String originalName = data.has("file_name") ? data.get("file_name").asText() : "audio.wav";
                                byte[] audioBytes = BotActionExtend.download(bot, event, fileId);

//                                if (!OtomadHelper.isAudioBytes(audioBytes)) {
//                                    bot.sendMsg(event, "这不是一个音频文件!", false);
//                                    return;
//                                }

                                try {
                                    // 转 WAV
                                    byte[] wavBytes = AudioTool.convertToWav(audioBytes);

                                    MsgTool.reply(bot, event, "正在处理中，请稍等片刻……");

                                    // 创建临时目录和文件，保留原文件名并加 "_processed" 后缀
                                    Path tempDir = Path.of("./temp");
                                    if (!Files.exists(tempDir)) Files.createDirectories(tempDir);

                                    String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                                    if (!safeName.toLowerCase().endsWith(".wav")) {
                                        safeName = safeName + ".wav";
                                    }
                                    Path tempFilePath = tempDir.resolve(safeName.replace(".wav", "_processed.wav"));
                                    Files.write(tempFilePath, wavBytes);

                                    // Python 脚本绝对路径
                                    String pyPath = new File("./script/PitchCorrection4Mirai.py").getAbsolutePath();
                                    String audioPath = tempFilePath.toAbsolutePath().toString();

                                    // 执行 Python 脚本
                                    ProcessBuilder pb = new ProcessBuilder("python", pyPath, audioPath);
                                    pb.inheritIO();
                                    Process process = pb.start();
                                    int code = process.waitFor();
                                    if (code != 0) {
                                        bot.sendMsg(event, "修音失败: Python 脚本返回错误代码 " + code, false);
                                        return;
                                    }

                                    // Python 输出文件
                                    Path resultPath = Path.of(audioPath + ".result.wav");
                                    if (!Files.exists(resultPath)) {
                                        bot.sendMsg(event, "修音失败: 处理后的文件不存在", false);
                                        return;
                                    }

                                    String fileName = resultPath.getFileName().toString();
                                    if (event.getGroupId() == null) {
                                        bot.uploadPrivateFile(event.getUserId(), resultPath.toAbsolutePath().toString(), fileName);
                                    } else {
                                        bot.uploadGroupFile(event.getGroupId(), resultPath.toAbsolutePath().toString(), fileName);
                                    }

                                    Files.deleteIfExists(tempFilePath);
                                    Files.deleteIfExists(resultPath);

                                } catch (Exception e) {
                                    bot.sendMsg(event, "修音失败: " + e.getMessage(), false);
                                    log.error("Error: ", e);
                                }
                            });
                        })
        );
    }
}