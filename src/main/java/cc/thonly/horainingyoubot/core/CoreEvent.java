package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.Constants;
import cc.thonly.horainingyoubot.event.*;
import cc.thonly.horainingyoubot.event.internal.EventResult;
import cc.thonly.horainingyoubot.event.internal.EventBusFactory;
import cc.thonly.horainingyoubot.event.internal.EventKey;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Objects;

@Component("botCoreEvent")
public class CoreEvent {
    public static final EventKey<ReceiveAnyEvent> RECEIVE_ANY = EventBusFactory.key(ReceiveAnyEvent.class);
    public static final EventKey<ReceiveAnyReplyEvent> RECEIVE_ANY_REPLY = EventBusFactory.key(ReceiveAnyReplyEvent.class);
    public static final EventKey<AtBotEvent> AT_BOT = EventBusFactory.key(AtBotEvent.class);
    public static final EventKey<PokeEvent> POKE = EventBusFactory.key(PokeEvent.class);
    public static final EventKey<ReceiveFriendAddRequestEvent> FRIEND_ADD_REQUEST = EventBusFactory.key(ReceiveFriendAddRequestEvent.class);
    public static final EventKey<ReceiveGroupAddRequestEvent> GROUP_ADD_REQUEST = EventBusFactory.key(ReceiveGroupAddRequestEvent.class);

    public void handleBus(Bot bot, AnyMessageEvent event) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        if (!arrayMsg.isEmpty()) {
            ArrayMsg first = arrayMsg.getFirst();
            if (Objects.equals(first.getType(), MsgTypeEnum.poke)) {
                EventResult result = POKE.post(new PokeEvent(bot, event));
                if (result.is(EventResult.BLOCKING)) {
                    return;
                }
            }
            if (Objects.equals(first.getType(), MsgTypeEnum.at)) {
                JsonNode data = first.getData();
                String qq = data.get("qq").asString();
                if (Objects.equals(qq, Constants.CACHED_SELF_ID)) {
                    EventResult result = AT_BOT.post(new AtBotEvent(bot, event));
                    if (result.is(EventResult.BLOCKING)) {
                        return;
                    }
                }
            }
            if (Objects.equals(first.getType(), MsgTypeEnum.reply)) {
                JsonNode data = first.getData();
                GroupMessageEvent.GroupSender sender = event.getSender();
                Long senderId = sender.getUserId();
                String id = data.get("id").asString();
                //noinspection WrapperTypeMayBePrimitive
                Integer ipi = Integer.parseInt(id);
                RECEIVE_ANY_REPLY.post(new ReceiveAnyReplyEvent(bot, event, ipi.longValue(), senderId));
            }
        }
        EventResult result = RECEIVE_ANY.post(new ReceiveAnyEvent(bot, event));
    }
}
