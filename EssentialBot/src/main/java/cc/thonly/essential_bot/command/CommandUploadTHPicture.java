package cc.thonly.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.service.DataManagerImpl;
import cc.thonly.horainingyoubot.service.RecordLogServiceImpl;
import cc.thonly.horainingyoubot.util.HTTPReq;
import cc.thonly.horainingyoubot.util.LinkedMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Command
public class CommandUploadTHPicture implements CommandEntrypoint {
    private static final Gson gson = new Gson();

    @Autowired
    RecordLogServiceImpl recordLogService;

    @Autowired
    DataManagerImpl dataManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("上传东方图")
                        .withExecutor((bot, event, args) -> {

                            LinkedMessage.start(bot, event, ctx -> this.handle(bot, event, ctx),(b, e) -> {

                            });
                        })
        );

    }


    public void handle(Bot bot, AnyMessageEvent event, LinkedMessage.Context ctx) {
        bot.sendMsg(event, ArrayMsgUtils.builder()
                        .reply(event.getMessageId())
                        .text("请发送待上传图片").build(),
                false);
        AnyMessageEvent next = ctx.waitNext(15);
        System.out.println(next);
        if (next.getMessage().contains("./cancel")) {
            bot.sendMsg(event, ArrayMsgUtils.builder()
                            .text("已取消").build(),
                    false);
            return;
        }
        List<ArrayMsg> list = next.getArrayMsg()
                .stream()
                .filter(arrayMsg -> Objects.equals(arrayMsg.getType(), MsgTypeEnum.image))
                .toList();
        List<String> urls = new ArrayList<>();
        for (ArrayMsg arrayMsg : list) {
            JsonNode data = arrayMsg.getData();
            JsonNode url = data.get("url");
            if (url == null) continue;
            urls.add(url.stringValue());
        }
        if (urls.isEmpty()) {
            ctx.cancel();
            return;
        }
        List<byte[]> bytes = HTTPReq.downloadFiles(urls);
        List<byte[]> jpg = HTTPReq.asJPG(bytes);
        int cnt = 0;
        for (byte[] imgBytes : jpg) {
            try {
                String uploadId = UUID.randomUUID().toString();
                var infoObj = new InfoObj(uploadId, event.getUserId());
                this.dataManager.save("touhou_image/" + uploadId + ".jpg", imgBytes);
                this.recordLogService.writeRecordLog("th_pic_uploader", gson.toJson(infoObj.toJson()));
                cnt++;
            } catch (Exception e) {
                log.error("Can't upload pic: ", e);
            }
        }
        bot.sendMsg(event, ArrayMsgUtils.builder()
                .reply(event.getMessageId())
                .text("成功上传%s张图片，失败%s\n若需要继续上传图片请".formatted(cnt, jpg.size() - cnt))
                .build(), false);
        this.handle(bot, event, ctx);
    }

    @Getter
    public static class InfoObj {
        private final String id;
        private final Long uploader;

        public InfoObj(String id, Long uploader) {
            this.id = id;
            this.uploader = uploader;
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", this.id);
            jsonObject.addProperty("uploader", this.uploader);
            return jsonObject;
        }
    }
}