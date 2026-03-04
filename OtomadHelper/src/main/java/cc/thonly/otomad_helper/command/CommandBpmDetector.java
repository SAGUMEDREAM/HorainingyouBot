package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.command.*;
import cc.thonly.horainingyoubot.util.*;
import cc.thonly.horainingyoubot.util.AudioTool;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Command
public class CommandBpmDetector implements CommandEntrypoint {

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("测bpm")
                        .withAliasName("测BPM")
                        .withAliasName("帮我测bpm")
                        .withAliasName("帮我测BPM")
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
                                    // 1. 转 WAV
                                    byte[] wavBytes = AudioTool.convertToWav(audioBytes);

                                    MsgTool.reply(bot, event, "正在处理中，请稍等片刻……");

                                    Path tempDir = Path.of("./temp");
                                    if (!Files.exists(tempDir)) Files.createDirectories(tempDir);

                                    String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                                    if (!safeName.toLowerCase().endsWith(".wav")) safeName += ".wav";

                                    Path tempFilePath = tempDir.resolve(safeName);
                                    Files.write(tempFilePath, wavBytes);

                                    String pyPath = new File("./script/BpmDetector.py").getAbsolutePath();
                                    String audioPath = tempFilePath.toAbsolutePath().toString();

                                    ProcessBuilder pb = new ProcessBuilder("python", pyPath, audioPath);
                                    pb.redirectErrorStream(true);
                                    pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
                                    Process process = pb.start();

                                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    StringBuilder output = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        output.append(line).append("\n");
                                    }

                                    int code = process.waitFor();
                                    if (code != 0) {
                                        bot.sendMsg(event, "检测失败: Python 脚本返回错误代码 " + code, false);
                                        return;
                                    }

                                    String bpmResult = output.toString().trim();
                                    MsgTool.reply(bot, event, ("BPM 检测结果: " + bpmResult).replaceAll("No audio data for sample, skipping...", ""));

                                    Files.deleteIfExists(tempFilePath);
                                } catch (Exception e) {
                                    bot.sendMsg(event, "检测失败: " + e.getMessage(), false);
                                    log.error("Error: ", e);
                                }
                            });
                        })
        );
    }
}