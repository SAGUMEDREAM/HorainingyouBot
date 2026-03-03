package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.controller.TempFileController;
import cc.thonly.essential_bot.util.HuoZiYinShuaKtProxy;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
@Command
public class CommandHuoZi implements CommandEntrypoint {
    private static final String BASE_PATH = "static/assets/huozi/sounds/";
    private final Map<String, byte[]> name2AudioData = new HashMap<>(128);

    @Autowired
    TempFileController tempFileController;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("活字印刷")
                        .withArguments("#{texts}")
                        .withAliasName("huozi")
                        .withExecutor((bot, event, args) -> {
                            String texts = args.getString("texts");
                            if (texts == null) {
                                return;
                            }
                            byte[] bytes = HuoZiYinShuaKtProxy.huoZiYinShua(texts);

                            // 先判断拼接结果是否为空
                            if (bytes != null) {
                                UUID audioId = this.tempFileController.saveFile(bytes);
                                HuoZiYinShuaKtProxy.deleteTemp();
                                bot.sendMsg(event, ArrayMsgUtils.builder().voice("http://127.0.0.1:9920/api/temp_file/get_voice/%s".formatted(audioId)).build(), false);
                            } else {
                                bot.sendMsg(event, ArrayMsgUtils.builder().text("未找到相关音频文件。").build(), false);
                            }
                        })
        );
    }
}