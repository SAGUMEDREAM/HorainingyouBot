package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.maimai.Maimai;
import io.lemonjuice.flan_mai_plugin.api.CompletionTableGenerator;
import io.lemonjuice.flan_mai_plugin.api.SongPlayDataGenerator;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Slf4j
@Command
public class CommandCompletionTable implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("牌子")
                        .withArguments("#{name} #{user}")
                        .withExecutor((bot, event, args) -> {
                            String name = args.getString("name");
                            Optional<User> userOptional = args.getUser("user");
                            Long userId = null;
                            if (userOptional.isEmpty()) {
                                userId = event.getUserId();
                            } else {
                                userId = userOptional.get().getUserId();
                            }
                            BufferedImage image = CompletionTableGenerator.generateWithPlates(userId, name);
                            try {
                                byte[] data = Maimai.image2Bytes(image, "png");
                                MsgTool.reply(bot, event, MsgTool.img(data));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        })
                        .up()
        );
    }
}
