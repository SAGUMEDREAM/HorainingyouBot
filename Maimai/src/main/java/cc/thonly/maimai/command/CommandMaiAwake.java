package cc.thonly.maimai.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import cc.thonly.horainingyoubot.util.MsgTool;
import cc.thonly.maimai.Maimai;
import cc.thonly.maimai.util.ImageUtils;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Command
public class CommandMaiAwake implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("旅行伙伴觉醒")
                        .withExecutor((bot, event, args) -> {
                            LinkedMessage.start(bot, event, ctx -> {
                                MsgTool.reply(bot, event, "请发送待处理图片");
                                AnyMessageEvent next = ctx.waitNext(15);
                                if (next.getMessage().contains("./cancel")) {
                                    MsgTool.reply(bot, event, "操作已取消");
                                    return;
                                }
                                List<ArrayMsg> list = next.getArrayMsg().stream().filter(item -> item.getType() == MsgTypeEnum.image).toList();
                                if (list.isEmpty()) {
                                    MsgTool.reply(bot, event, "操作已取消");
                                    return;
                                }
                                ArrayMsg first = list.getFirst();
                                JsonNode data = first.getData();
                                String imageUrl = data.get("url").asString();
                                byte[] image = MsgTool.getImageFromUrl(imageUrl);

                                byte[] bytes = ImageUtils.generateAvatar(Maimai.class, "/static/assets/mai_awake.png", image);
                                MsgTool.reply(bot, event,
                                        ArrayMsgUtils.builder().img(bytes)
                                                .build()
                                );
                            });
                        })
        );

    }
}