package cc.thonly.touhou_bot;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.EveryEvents;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.horainingyoubot.event.internal.EventResult;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.touhou_bot.command.*;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TouhouBot implements JPlugin {
    @Autowired
    Commands commands;

    @Override
    public void onInitialize() {
        this.registerCommands(this.commands);
        this.registerEvents();
    }

    @Override
    public void registerEvents() {
        EveryEvents.RECEIVE_ANY.register(listener -> {
            Bot bot = listener.getBot();
            AnyMessageEvent event = listener.getEvent();
            List<ArrayMsg> arrayMsg = event.getArrayMsg();
            if (Objects.equals(event.getUserId(), event.getSelfId())) {
                return EventResult.PASS;
            }
            if (MsgTool.startsWith(arrayMsg, "1+1")) {
                MsgTool.reply(bot, event, "=⑨!");
            }
            return EventResult.PASS;
        });
        EveryEvents.RECEIVE_ANY.register(listener -> {
            Bot bot = listener.getBot();
            AnyMessageEvent event = listener.getEvent();
            if (Objects.equals(event.getUserId(), event.getSelfId())) {
                return EventResult.PASS;
            }
            List<ArrayMsg> arrayMsg = event.getArrayMsg();
            if (MsgTool.startsWith(arrayMsg, "baka")) {
                bot.sendMsg(event, "BAKA!", false);
                return EventResult.PASS;
            }
            if (MsgTool.startsWith(arrayMsg, "BAKA!")) {
                String msgStr = MsgTool.toString(arrayMsg);

                long exclamations = 0;
                exclamations = msgStr.chars().filter(c -> c == '!').count() + 1;
                if (exclamations > 8) {
                    List<String> cuteReplies = List.of(
                            "哇哇哇，被欺负了好伤心～",
                            "呜呜，被欺负了啦～QAQ",
                            "哎呀呀，不要欺负我嘛～(>_<)",
                            "呜呜，好委屈呀～(｡•́︿•̀｡)",
                            "哇哇，被欺负了怎么办～Σ(っ°Д°;)っ"
                    );
                    Random random = new Random();
                    String reply = cuteReplies.get(random.nextInt(cuteReplies.size()));
                    bot.sendMsg(event, reply, false);
                    return EventResult.PASS;
                }

                String reply = "BAKA!" + "!".repeat((int) Math.max(0, exclamations - 1));
                bot.sendMsg(event, reply, false);
                return EventResult.PASS;
            }
            return EventResult.PASS;
        });
    }

    @Autowired
    CommandJrrp commandJrrp;
    @Autowired
    CommandRandomTouhouMusic commandRandomTouhouMusic;
    @Autowired
    CommandUploadTHPicture commandUploadTHPicture;
    @Autowired
    CommandTHPicture commandTHPicture;
    @Autowired
    CommandTHSearch commandTHSearch;
    @Autowired
    CommandTHWiki commandTHWiki;
    @Autowired
    CommandGroupSearch commandGroupSearch;
    @Autowired
    CommandLilySearch commandLilySearch;
    @Autowired
    CommandVoice commandVoice;

    @Override
    public void registerCommands(Commands commands) {
        this.commands.registerCommand(this.commandRandomTouhouMusic);
        this.commands.registerCommand(this.commandUploadTHPicture);
        this.commands.registerCommand(this.commandTHPicture);
        this.commands.registerCommand(this.commandTHSearch);
        this.commands.registerCommand(this.commandGroupSearch);
        this.commands.registerCommand(this.commandTHWiki);
        this.commands.registerCommand(this.commandLilySearch);
        this.commands.registerCommand(this.commandJrrp);
        this.commands.registerCommand(this.commandVoice);
    }

    @Override
    public String getPluginId() {
        return "touhou_bot";
    }
}
