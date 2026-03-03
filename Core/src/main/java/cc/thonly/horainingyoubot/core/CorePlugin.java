package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.Constants;
import cc.thonly.horainingyoubot.data.CommandResult;
import cc.thonly.horainingyoubot.data.PermissionLevel;
import cc.thonly.horainingyoubot.data.db.Group;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.event.PokeEvent;
import cc.thonly.horainingyoubot.event.ReceiveFriendAddRequestEvent;
import cc.thonly.horainingyoubot.event.ReceiveGroupAddRequestEvent;
import cc.thonly.horainingyoubot.repository.GroupRepository;
import cc.thonly.horainingyoubot.repository.UserRepository;
import cc.thonly.horainingyoubot.service.GroupManagerImpl;
import cc.thonly.horainingyoubot.service.MessageFilterImpl;
import cc.thonly.horainingyoubot.service.MessageHandlerImpl;
import cc.thonly.horainingyoubot.service.UserManagerImpl;
import cc.thonly.horainingyoubot.util.BotActionExtend;
import cc.thonly.horainingyoubot.util.DeferTask;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.StrangerInfoResp;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Shiro
@Component
@Slf4j
public class CorePlugin extends BotPlugin {
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static int START_ID = 0;

    public static void onLoad() {
        scheduler.scheduleAtFixedRate(() -> {
            DeferTask.tick(START_ID);
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserManagerImpl userManager;

    @Autowired
    GroupManagerImpl groupManager;

    @Autowired
    MessageHandlerImpl handler;

    @Autowired
    CoreEvent coreEvent;

    @Autowired
    MessageFilterImpl messageFilter;

    @Override
    public int onGroupPokeNotice(Bot bot, PokeNoticeEvent event) {
        User user = this.userManager.getOrCreate(event);
        if (user.getBanned()) {
            return MESSAGE_BLOCK;
        }
        CoreEvent.POKE.post(new PokeEvent(bot, event));
        return super.onGroupPokeNotice(bot, event);
    }

    @Override
    public int onPrivatePokeNotice(Bot bot, PokeNoticeEvent event) {
        User user = this.userManager.getOrCreate(event);
        if (user.getBanned()) {
            return MESSAGE_BLOCK;
        }
        CoreEvent.POKE.post(new PokeEvent(bot, event));
        return super.onPrivatePokeNotice(bot, event);
    }

    @Override
    public int onFriendAddRequest(Bot bot, FriendAddRequestEvent event) {
        User user = this.userManager.getOrCreate(event);
        if (user.getBanned()) {
            return MESSAGE_BLOCK;
        }
        CoreEvent.FRIEND_ADD_REQUEST.post(new ReceiveFriendAddRequestEvent(bot, event));
        return super.onFriendAddRequest(bot, event);
    }

    @Override
    public int onGroupAddRequest(Bot bot, GroupAddRequestEvent event) {
        User user = this.userManager.getOrCreate(event);
        if (user.getBanned()) {
            return MESSAGE_BLOCK;
        }
        CoreEvent.GROUP_ADD_REQUEST.post(new ReceiveGroupAddRequestEvent(bot, event));
        return super.onGroupAddRequest(bot, event);
    }

    @Override
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        if (Constants.DEV_MODE && !isDevUser(bot, event)) {
            return MESSAGE_BLOCK;
        }
        System.out.println("==");
        System.out.println(event.getArrayMsg());
        System.out.println(event.getRawMessage());

        User user = this.userManager.getOrCreate(event);
        Group group = null;
        boolean groupEvent = event.getGroupId() != null;
        if (groupEvent) {
            group = this.groupManager.getOrCreate(event);
//            System.out.println(group);
            if (group.getBanned() && !user.hasPermissionLevel(PermissionLevel.MODERATOR)) {
                return MESSAGE_BLOCK;
            }
        }
        if (user.getBanned()) {
            return MESSAGE_BLOCK;
        }
        if (event.getUserId() != bot.getSelfId()) {
//            bot.sendMsg(
//                    event,
//                    ArrayMsgUtils.builder().img("https://i1.hdslb.com/bfs/archive/632983c8aa3feeb11c8b0db8c4fb378e71875400.jpg").build(),
//                    false
//            );
        }
        if (!this.messageFilter.isAllowContent(event.getMessage())) {
            return MESSAGE_BLOCK;
        }
//        System.out.println(111);
        LinkedMessage.onMessage(event);
        CommandResult result = this.handler.accept(bot, user, group, event);
        if (Objects.equals(result, CommandResult.NO_PERMISSION)) {
            bot.sendMsg(event, ArrayMsgUtils.builder().reply(event.getMessageId()).text("您没有执行此命令的权限").build(), true);
        }
        if (!user.hasAcceptedEula()) {
            return MESSAGE_BLOCK;
        }
        if (!Objects.equals(result, CommandResult.SUCCESS)) {
            this.coreEvent.handleBus(bot, event);
        }
//        if (event.getMessage().contains("./开启链式测试")) {
//
//            bot.sendMsg(event, "已启动，请连续输入 5 次", false);
//
//            LinkedMessage.start(bot, event, ctx -> {
//
//                for (int i = 0; i < 5; i++) {
//                    try {
//                        AnyMessageEvent next = ctx.waitNext(5);
//
//                        bot.sendMsg(next,
//                                "第 %d 次，你输入了: %s".formatted(i + 1, next.getMessage()),
//                                false);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        bot.sendMsg(ctx.getOrigin(),
//                                "等待超时，流程结束",
//                                false);
//                        break;
//                    }
//                }
//
//            }, (b, e) -> {
//                bot.sendMsg(event, "链式流程异常终止", false);
//            });
//        }
        return MESSAGE_IGNORE;
    }

    private boolean isDevUser(Bot bot, AnyMessageEvent event) {
        return event.getUserId() == bot.getSelfId() || event.getUserId() == 807131829L || event.getUserId() == 2662728128L;
    }
}
