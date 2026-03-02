package cc.thonly.horainingyoubot.util;

import cc.thonly.horainingyoubot.data.db.User;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import tools.jackson.databind.JsonNode;

@SuppressWarnings("JavaExistingMethodCanBeUsed")
@Slf4j
public class MsgTool {
    public static CloseableHttpClient createHttpClient() throws Exception {
        TrustStrategy trustAllStrategy = (chain, authType) -> true;

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(trustAllStrategy)
                .build();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        return HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
    }

    public static boolean isAtBot(ArrayMsg msg, Bot bot) {
        if (!Objects.equals(msg.getType(), MsgTypeEnum.at)) {
            return false;
        }
        JsonNode data = msg.getData();
        String qq = data.get("qq").asString();
        return Objects.equals(qq, String.valueOf(bot.getSelfId()).replace("L", ""));
    }

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

    public static String encodeURIComponent(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static String decodeURIComponent(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    public static ArrayMsg img2Byte(String url) {
        try {
            return img(_img2Byte(url));
        } catch (Exception e) {
            log.error("下载图片异常, url={}", url, e);
            return null;
        }
    }

    static byte[] _img2Byte(String url) {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    log.error("Failed to download image, status code: {}", response.getStatusLine());
                    return null;
                }

                InputStream inputStream = response.getEntity().getContent();
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            log.error("Download error: ",e);
            return null;
        }
    }

    public static ArrayMsg text(String text) {
        return ArrayMsgUtils.builder().text(text).build().getFirst();
    }

    public static ArrayMsg img(String url) {
        return ArrayMsgUtils.builder().img(url).build().getFirst();
    }

    public static ArrayMsg img(byte[] url) {
        return ArrayMsgUtils.builder().img(url).build().getFirst();
    }

    public static ArrayMsg video(String url, String cover) {
        return ArrayMsgUtils.builder().video(url, cover).build().getFirst();
    }

    public static ArrayMsg voice(String url) {
        return ArrayMsgUtils.builder().voice(url).build().getFirst();
    }

    public static ArrayMsg at(Long userId) {
        return ArrayMsgUtils.builder().at(userId).build().getFirst();
    }

    public static ArrayMsg at(User user) {
        return ArrayMsgUtils.builder().at(user.getUserId()).build().getFirst();
    }

    public static List<ArrayMsg> replaceAll(String source, String charset, ArrayMsg target) {
        List<ArrayMsg> result = new ArrayList<>();
        String[] split = source.split(charset);
        for (int i = 0; i < split.length; i++) {
            result.add(ArrayMsgUtils.builder().text(split[i]).build().getFirst());
            if (i < split.length - 1) {
                result.add(target);
            }
        }
        return result;
    }

    public static ArrayMsg reply(AnyMessageEvent event) {
        return ArrayMsgUtils.builder().reply(event.getMessageId()).build().getFirst();
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

    public static void reply(Bot bot, AnyMessageEvent event, ArrayMsg text) {
        List<ArrayMsg> rpl = ArrayMsgUtils.builder()
                .reply(event.getMessageId())
                .build();
        if (rpl.isEmpty()) {
            return;
        }
        ArrayMsg first = rpl.getFirst();
        bot.sendMsg(event,
                List.of(first, text),
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

    public static void reply(Bot bot, AnyMessageEvent event, Consumer<List<ArrayMsg>> text) {
        ArrayList<ArrayMsg> list = new ArrayList<>();
        ArrayMsg header = ArrayMsgUtils.builder()
                .reply(event.getMessageId())
                .build().getFirst();
        list.add(header);
        text.accept(list);
        bot.sendMsg(event,
                list,
                false
        );
    }

    public static String toStrCQ(ArrayMsg arrayMsg) {
        return arrayMsg.toCQCode();
    }

    public static String toListCQ(List<ArrayMsg> list) {
        return list.stream().map(ArrayMsg::toCQCode).collect(Collectors.joining());
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
