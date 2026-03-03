package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.maimai.Maimai;
import io.lemonjuice.flan_mai_plugin.api.DivingFishB50Generator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Slf4j
@Command
public class CommandB50 implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("b50")
                        .withExecutor((bot, event, args) -> {
                            Long userId = event.getUserId();
                            BufferedImage image = DivingFishB50Generator.generate(userId);
                            try {
                                byte[] data = Maimai.image2Bytes(image, "png");
                                MsgTool.reply(bot, event, MsgTool.img(data));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        })
                        .up()
                        .addNode("other", (bot, event, args) -> {
                            Optional<User> userOptional = args.getUser("user_id");
                            if (userOptional.isEmpty()) {
                                return;
                            }

                            User user = userOptional.get();
                            BufferedImage image = DivingFishB50Generator.generate(user.getUserId());
                            try {
                                byte[] data = Maimai.image2Bytes(image, "png");
                                MsgTool.reply(bot, event, MsgTool.img(data));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        })
                        .withArguments("#{user_id}")
                        .up()
        );

    }



}