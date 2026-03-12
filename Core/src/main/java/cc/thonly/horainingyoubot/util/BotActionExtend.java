package cc.thonly.horainingyoubot.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.handler.ActionHandler;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SuppressWarnings("DuplicatedCode")
public class BotActionExtend {
    public static final ActionPath DOWNLOAD = createKey("download_file");
    public static final ActionPath GET_PRIVATE_FILE_URL = createKey("get_private_file_url");
    public static final ActionPath GET_GROUP_FILE_URL = createKey("get_group_file_url");
    public static final ActionPath ACCEPT_GROUP_INVITATION = createKey("accept_group_invitation");
    public static final ActionPath REJECT_GROUP_INVITATION = createKey("reject_group_invitation");
    private static final Gson gson = new Gson();
    private static final OkHttpClient httpClient = new OkHttpClient();

    public static ActionPath createKey(String path) {
        return () -> path;
    }

    public static JsonElement acceptGroupInvitation(Bot bot, Long groupId, Long invitationSeq) {
        WebSocketSession session = bot.getSession();
        ActionHandler actionHandler = bot.getActionHandler();
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParamsExtend.INVITATION_SEQ, invitationSeq);
        JsonObjectWrapper result;
        result = actionHandler.action(session, ACCEPT_GROUP_INVITATION, params);
        String jsonString = result.toJSONString();
        return JsonParser.parseString(jsonString);
    }

    public static JsonElement rejectGroupInvitation(Bot bot, Long groupId, Long invitationSeq) {
        WebSocketSession session = bot.getSession();
        ActionHandler actionHandler = bot.getActionHandler();
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParamsExtend.INVITATION_SEQ, invitationSeq);
        JsonObjectWrapper result;
        result = actionHandler.action(session, REJECT_GROUP_INVITATION, params);
        String jsonString = result.toJSONString();
        return JsonParser.parseString(jsonString);
    }

    public static byte[] download(Bot bot, AnyMessageEvent event, String fileId) {
        WebSocketSession session = bot.getSession();
        ActionHandler actionHandler = bot.getActionHandler();
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.FILE_ID, fileId);

        JsonObjectWrapper result;
        if (event.getGroupId() == null) {
            result = actionHandler.action(session, GET_PRIVATE_FILE_URL, params);
        } else {
            params.put(ActionParams.GROUP_ID, event.getGroupId());
            result = actionHandler.action(session, GET_GROUP_FILE_URL, params);
        }

        if (result == null) return null;

        String jsonString = result.toJSONString();
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        if (!jsonElement.isJsonObject()) return null;

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject data = jsonObject.getAsJsonObject("data");
        if (data == null || !data.has("url")) return null;

        String url = data.get("url").getAsString();

        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;
            ResponseBody body = response.body();
            if (body == null) return null;
            return body.bytes();
        } catch (IOException e) {
            log.error("Error: ", e);
            return null;
        }
    }
}
