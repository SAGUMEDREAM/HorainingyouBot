package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.maimai.Maimai;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.model.ArrayMsg;
import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Command
public class CommandMaiAlias implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("别名")
                        .withArguments("#{name}")
                        .withExecutor((bot, event, args) -> {
                            String name = args.getString("name");
                            if (name == null) {
                                return;
                            }
                            MsgTool.reply(bot, event, "获取中...");
                            List<Song> songByAlias = SongManager.getSongByAlias(name);
                            if (songByAlias.isEmpty()) {
                                MsgTool.reply(bot, event, "暂无结果");
                                return;
                            }
                            List<byte[]> list = new ArrayList<>();
                            for (Song byAlias : songByAlias) {
                                BufferedImage image = SongInfoGenerator.generate(byAlias.id);
                                try {
                                    byte[] data = Maimai.image2Bytes(image, "png");
                                    //noinspection ConstantValue
                                    if (data != null && data.length != 0) {
                                        list.add(data);
                                    }
                                } catch (Exception e) {
                                    log.error("Error: ", e);
                                }
                            }
                            ArrayMsgUtils builder = ArrayMsgUtils.builder();
                            int i = 0;
                            for (byte[] bytes : list) {
                                builder = builder.img(bytes);
                                i++;
                                if (i > 7) {
                                    break;
                                }
                            }
                            MsgTool.reply(bot, event, builder.build());
                        })
                        .up()
        );

    }
}