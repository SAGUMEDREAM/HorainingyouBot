package cc.thonly.touhou_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.controller.TempFileController;
import cc.thonly.horainingyoubot.util.HTTPReq;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.horainingyoubot.util.PinyinToKana;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Command
public class CommandVoice implements CommandEntrypoint {

    @Autowired
    TempFileController tempFileController;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("说话")
                        .withArguments("#{text}")
                        .withExecutor((bot, event, args) -> {
                            String text = args.getString("text");
                            if (text == null) {
                                return;
                            }
                            String kana = PinyinToKana.parse(text);
                            String api = "https://www.yukumo.net/api/v2/aqtk2/koe.mp3?type=f3a&kanji=%s".formatted(kana);
                            byte[] bytes = HTTPReq.downloadFile(api);
                            if (bytes == null) {
                                MsgTool.reply(bot, event, "生成失败!");
                                return;
                            }
                            UUID audioId = this.tempFileController.saveFile(bytes);
                            bot.sendMsg(event, ArrayMsgUtils.builder().voice("http://127.0.0.1:9920/api/temp_file/get_voice/%s".formatted(audioId)).build(), false);
                        })
                        .up()
        );

    }
}