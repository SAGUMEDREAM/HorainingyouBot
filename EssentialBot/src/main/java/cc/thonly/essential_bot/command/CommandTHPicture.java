package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.service.DataManagerImpl;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Command
public class CommandTHPicture implements CommandEntrypoint {
    @Autowired
    DataManagerImpl dataManager;

    @Override
    @SneakyThrows
    public void registerCommand(Commands commands) {
        Path p = Path.of("./data/touhou_image/");
        if (Files.notExists(p)) {
            Files.createDirectories(p);
        }
        commands.registerCommand(
                CommandNode.createRoot("随机东方")
                        .withAliasName("随机东方图")
                        .withAliasName("random_touhou")
                        .withExecutor((bot, event, args) -> {
                            List<String> touhouImageNames = this.dataManager.getFileNames("touhou_image");
                            if (touhouImageNames.isEmpty()) {
                                bot.sendMsg(event, "当前文件夹为空!", false);
                                return;
                            }
                            String randomFileName = touhouImageNames.get(
                                    ThreadLocalRandom.current().nextInt(touhouImageNames.size())
                            );

                            byte[] bytes = this.dataManager.get("touhou_image/" + randomFileName);

                            if (bytes == null) {
                                bot.sendMsg(event, "图片读取失败!", false);
                                return;
                            }

                            bot.sendMsg(
                                    event,
                                    ArrayMsgUtils.builder()
                                            .reply(event.getMessageId())
                                            .img(bytes)
                                            .build(),
                                    false
                            );
                        })
        );
    }
}
