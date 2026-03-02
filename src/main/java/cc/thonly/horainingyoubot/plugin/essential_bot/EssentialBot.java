package cc.thonly.horainingyoubot.plugin.essential_bot;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.core.CoreEvent;
import cc.thonly.horainingyoubot.core.JPlugin;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.event.PokeEvent;
import cc.thonly.horainingyoubot.event.ReceiveAnyEvent;
import cc.thonly.horainingyoubot.event.ReceiveAnyReplyEvent;
import cc.thonly.horainingyoubot.event.internal.BotEventListener;
import cc.thonly.horainingyoubot.event.internal.EventResult;
import cc.thonly.horainingyoubot.plugin.essential_bot.command.*;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class EssentialBot implements JPlugin {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    Commands commands;

    @Autowired
    UserManagerImpl userManager;

    @Override
    public void onInitialize() {
        this.registerCommands();
        CoreEvent.AT_BOT.register(event -> {
            Bot bot = event.getBot();
            AnyMessageEvent anyMessageEvent = event.getEvent();
            List<ArrayMsg> arrayMsgs = anyMessageEvent.getArrayMsg();
            String cqcode = MsgTool.toListCQ(arrayMsgs);
            if (cqcode.contains("在吗") || cqcode.contains("在吗?") || cqcode.contains("在?")) {
                MsgTool.reply(bot, anyMessageEvent, "Bot在");
                return EventResult.BLOCKING;
            } else {
                return EventResult.PASS;
            }
        });
        AtomicBoolean pokeLock = new AtomicBoolean(false);
        CoreEvent.POKE.register(event -> {
            Bot bot = event.getBot();
            PokeNoticeEvent pokeNoticeEvent = event.getEvent();
            if (Objects.equals(pokeNoticeEvent.getUserId(), bot.getSelfId())) {
                return EventResult.PASS;
            }
            if (!Objects.equals(pokeNoticeEvent.getTargetId(), bot.getSelfId())) {
                return EventResult.PASS;
            }
            if (pokeLock.get()) {
                return EventResult.PASS;
            }
            if (pokeNoticeEvent.getGroupId() == null) {
                bot.sendPrivateMsg(pokeNoticeEvent.getUserId(), "喂!(#`O′) 戳我干什么!!", false);
            } else {
                bot.sendGroupMsg(pokeNoticeEvent.getGroupId(), "喂!(#`O′) 戳我干什么!!", false);
            }
            pokeLock.set(true);
            this.scheduler.schedule(() -> {
                pokeLock.set(false);
            }, 30, TimeUnit.SECONDS);
            return EventResult.BLOCKING;
        });
        CoreEvent.RECEIVE_ANY_REPLY.register(event -> {
            Bot bot = event.getBot();
            AnyMessageEvent anyMessageEvent = event.getEvent();
            List<ArrayMsg> arrayMsg = anyMessageEvent.getArrayMsg();
            ArrayMsg first = arrayMsg.getFirst();
            JsonNode data = first.getData();
            if (!Objects.equals(first.getType(), MsgTypeEnum.reply)) {
                return EventResult.PASS;
            }
            String messageIdStr = data.get("id").asString();
            int messageId = Integer.parseInt(messageIdStr);
            ActionData<MsgResp> msg = bot.getMsg(messageId);
            MsgResp msgRespData = msg.getData();
            Long userId = msgRespData.getUserId();
            if (!Objects.equals(userId, bot.getSelfId())) {
                return EventResult.PASS;
            }
            String listCQ = MsgTool.toListCQ(anyMessageEvent.getArrayMsg());
            if (listCQ.contains("不可以") || listCQ.contains("!d") || listCQ.contains("！D") || listCQ.contains("！d") || listCQ.contains("!D") || listCQ.contains("/闭嘴")) {
                User user = this.userManager.getOrCreate(anyMessageEvent);
                if (!user.hasPermissionLevel(2)) {
                    bot.sendMsg(anyMessageEvent, "哼,你管得着咱吗!?", false);
                    return EventResult.PASS;
                }
                bot.deleteMsg(messageId);
                bot.sendMsg(anyMessageEvent, "咱再也不乱说话了", false);
                return EventResult.BLOCKING;
            }
            return EventResult.PASS;
        });
        CoreEvent.RECEIVE_ANY.register(event -> {
            Bot bot = event.getBot();
            AnyMessageEvent anyMessageEvent = event.getEvent();
            List<ArrayMsg> arrayMsgList = anyMessageEvent.getArrayMsg();

            if (!arrayMsgList.isEmpty()) {
                ArrayMsg first = arrayMsgList.getFirst();
                if (MsgTypeEnum.text == first.getType()) {
                    JsonNode textNode = first.getData().get("text");
                    if (textNode.isString() &&
                            textNode.asString().toLowerCase().startsWith("owlpenguinparrot")) {
                        bot.sendMsg(anyMessageEvent, "干什么", false);
                    }
                }
            }
            return EventResult.PASS;
        });
    }

    @Autowired
    CommandKick commandKick;
    @Autowired
    CommandMute commandMute;
    @Autowired
    CommandUnmute commandUnmute;
    @Autowired
    CommandWholeMute commandWholeMute;
    @Autowired
    CommandLeaveMessage commandLeaveMessage;
    @Autowired
    CommandSign commandSign;
    @Autowired
    CommandInfo commandInfo;
    @Autowired
    CommandTHSearch commandTHSearch;
    @Autowired
    CommandGroupSearch commandGroupSearch;
    @Autowired
    CommandTHWiki commandTHWiki;
    @Autowired
    CommandLilySearch commandLilySearch;
    @Autowired
    CommandJrrp commandJrrp;
    @Autowired
    CommandTHPicture commandTHPicture;
    @Autowired
    CommandRandomAnime commandRandomAnime;
    @Autowired
    CommandRandomMember commandRandomMember;
    @Autowired
    CommandRandomNumber commandRandomNumber;
    @Autowired
    CommandRandomUUID commandRandomUUID;
    @Autowired
    CommandRandomTouhouMusic commandRandomTouhouMusic;
    @Autowired
    CommandUploadTHPicture commandUploadTHPicture;
    @Autowired
    CommandHuoZi commandHuozi;
    @Autowired
    CommandChoice commandChoice;
    @Autowired
    CommandFaBing commandFaBing;
    @Autowired
    CommandCrazyThursday commandCrazyThursday;
    @Autowired
    CommandBaiduImage commandBaiduImage;
    @Autowired
    CommandHomo commandHomo;
    @Autowired
    Command5k command5k;
    @Autowired
    CommandBa commandBa;
    @Autowired
    CommandXibao commandXibao;
    @Autowired
    CommandBeibao commandBeibao;
    @Autowired
    CommandAbbreviation commandAbbreviation;
    @Autowired
    CommandMarkdown commandMarkdown;
    @Autowired
    CommandPing commandPing;
    @Autowired
    CommandMCS commandMCS;
    @Autowired
    CommandCopyGroupMembers commandCopyGroupMembers;

    private void registerCommands() {
        this.commands.registerCommand(this.commandKick);
        this.commands.registerCommand(this.commandMute);
        this.commands.registerCommand(this.commandUnmute);
        this.commands.registerCommand(this.commandWholeMute);
        this.commands.registerCommand(this.commandLeaveMessage);
        this.commands.registerCommand(this.commandSign);
        this.commands.registerCommand(this.commandInfo);
        this.commands.registerCommand(this.commandTHSearch);
        this.commands.registerCommand(this.commandGroupSearch);
        this.commands.registerCommand(this.commandTHWiki);
        this.commands.registerCommand(this.commandLilySearch);
        this.commands.registerCommand(this.commandJrrp);
        this.commands.registerCommand(this.commandTHPicture);
        this.commands.registerCommand(this.commandRandomAnime);
        this.commands.registerCommand(this.commandRandomMember);
        this.commands.registerCommand(this.commandRandomNumber);
        this.commands.registerCommand(this.commandRandomUUID);
        this.commands.registerCommand(this.commandRandomTouhouMusic);
        this.commands.registerCommand(this.commandUploadTHPicture);
        this.commands.registerCommand(this.commandHuozi);
        this.commands.registerCommand(this.commandChoice);
        this.commands.registerCommand(this.commandFaBing);
        this.commands.registerCommand(this.commandCrazyThursday);
        this.commands.registerCommand(this.commandBaiduImage);
        this.commands.registerCommand(this.commandHomo);
        this.commands.registerCommand(this.command5k);
        this.commands.registerCommand(this.commandBa);
        this.commands.registerCommand(this.commandXibao);
        this.commands.registerCommand(this.commandBeibao);
        this.commands.registerCommand(this.commandAbbreviation);
        this.commands.registerCommand(this.commandMarkdown);
        this.commands.registerCommand(this.commandPing);
        this.commands.registerCommand(this.commandMCS);
        this.commands.registerCommand(this.commandCopyGroupMembers);
    }

    @Override
    public String getPluginId() {
        return "essential_bot";
    }
}
