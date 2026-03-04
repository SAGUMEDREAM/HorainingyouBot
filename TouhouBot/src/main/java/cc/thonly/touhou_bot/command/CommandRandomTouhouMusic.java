package cc.thonly.touhou_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.touhou_bot.data.music.MusicObject;
import cc.thonly.touhou_bot.serivce.THPlayListServiceImpl;
import cc.thonly.horainingyoubot.controller.TempFileController;
import cc.thonly.horainingyoubot.util.HTTPReq;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Command
public class CommandRandomTouhouMusic implements CommandEntrypoint {
    @Autowired
    THPlayListServiceImpl thPlayListService;

    @Autowired
    TempFileController tempFileController;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机东方原曲")
                        .withExecutor((bot, event, args) -> {
                            MusicObject musicObject = this.thPlayListService.random();
                            MsgTool.reply(bot, event, "名称：%s - %s".formatted(musicObject.getFrom(), musicObject.getName()));
                            byte[] bytes = HTTPReq.downloadFile(musicObject.getPath());
                            if (bytes == null) {
                                bot.sendMsg(event, ArrayMsgUtils.builder().text("文件下载失败").build(), false);
                                return;
                            }
                            UUID audioId = this.tempFileController.saveFile(bytes);
                            OneBotMedia file = OneBotMedia.builder().file("http://127.0.0.1:9920/api/temp_file/get_voice/%s".formatted(audioId)).cache(false);
                            bot.sendMsg(event, ArrayMsgUtils.builder().voice(file).build(), false);
                        })
        );

    }
}