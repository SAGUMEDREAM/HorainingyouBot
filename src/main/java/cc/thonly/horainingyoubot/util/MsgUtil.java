package cc.thonly.horainingyoubot.util;

import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
public class MsgUtil extends ArrayMsgUtils {
    public static String getGroupAvatar(long groupId, int size) {
        return String.format("https://p.qlogo.cn/gh/%s/%s/%s", groupId, groupId, size);
    }

    public static String getGroupAvatar(long groupId) {
        return String.format("https://p.qlogo.cn/gh/%s/%s/640", groupId, groupId);
    }

    public static String getUserAvatar(long userId, int size) {
        return String.format("https://q2.qlogo.cn/headimg_dl?dst_uin=%s&spec=%s", userId, size);
    }

    public static String getUserAvatar(long userId) {
        return String.format("https://q2.qlogo.cn/headimg_dl?dst_uin=%s&spec=640", userId);
    }

    public static List<Map<String, Object>> createForwardMsg(long selfId, String nickname, List<String> msgList) {
        return ShiroUtils.generateForwardMsg(selfId, nickname, msgList);
    }

    public static void reply(Bot bot, AnyMessageEvent event, String text) {
        bot.sendMsg(event,
                ArrayMsgUtils.builder()
                        .reply(event.getMessageId())
                        .text(text)
                        .build(),
                false
        );
    }

    public static void reply(Bot bot, AnyMessageEvent event, List<ArrayMsg> text) {
        List<ArrayMsg> rpl = ArrayMsgUtils.builder()
                .reply(event.getMessageId())
                .build();
        if (rpl.isEmpty()) {
            return;
        }
        ArrayMsg first = rpl.getFirst();
        List<ArrayMsg> list = new ArrayList<>();
        list.add(first);
        list.addAll(text);
        bot.sendMsg(event,
                list,
                false
        );
    }

    public static List<ArrayMsg> commitTexts(List<String> list) {
        ArrayMsgUtils builder = ArrayMsgUtils.builder();
        List<String> mapped = list.stream().map(s -> s + "\n").toList();
        StringBuilder mir = new StringBuilder();
        for (String s : mapped) {
            mir.append(s);
        }

        ArrayMsgUtils text = builder.text(mir.toString());
        return new ArrayList<>(text.build());
    }

    public static MsgUtils createMessage() {
        return MsgUtils.builder();
    }
}
