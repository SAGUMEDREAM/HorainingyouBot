package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.plugin.essential_bot.data.music.MusicObject;
import cc.thonly.horainingyoubot.plugin.essential_bot.serivce.THPlayListManager;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CommandRandomTouhouMusic implements CommandEntrypoint {
    @Autowired
    THPlayListManager thPlayListManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机东方原曲")
                        .withExecutor((bot, event, args) -> {
                            MusicObject musicObject = this.thPlayListManager.random();
                            MsgTool.reply(bot, event, "名称：%s - %s".formatted(musicObject.getFrom(), musicObject.getName()));
                            OneBotMedia file = OneBotMedia.builder().file(musicObject.getPath()).cache(false);
                            bot.sendMsg(event, ArrayMsgUtils.builder().voice(file).build(), false);
                        })
        );

    }
}