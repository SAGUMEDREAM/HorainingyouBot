package cc.thonly.horainingyoubot.util;

import cc.thonly.horainingyoubot.controller.TempFileController;
import cc.thonly.horainingyoubot.core.SpringContextHolder;
import cc.thonly.horainingyoubot.data.db.User;
import cc.thonly.horainingyoubot.event.internal.EventResult;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static byte[] getImageFromUrl(String url) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try {

                URL imageUrl = URI.create(url).toURL();

                BufferedImage image = ImageIO.read(imageUrl);

                if (image != null) {
                    ImageIO.write(image, "PNG", baos);
                    baos.flush();
                    return baos.toByteArray();
                } else {
                    throw new IOException("Failed to read image from URL.");
                }
            } catch (IOException e) {
                log.error("Error: ", e);
                return null;
            }
        } catch (IOException e) {
            log.error("Error: ", e);
            return null;
        }
    }

    public static Integer getReplyMessageId(Bot bot, AnyMessageEvent event) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        ArrayMsg first = arrayMsg.getFirst();
        if (!Objects.equals(first.getType(), MsgTypeEnum.reply)) {
            return null;
        }
        JsonNode data = first.getData();
        if (!Objects.equals(first.getType(), MsgTypeEnum.reply)) {
            return null;
        }
        String messageIdStr = data.get("id").asString();
        return Integer.parseInt(messageIdStr);
    }

    public static Long getReplyTarget(Bot bot, AnyMessageEvent event) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        ArrayMsg first = arrayMsg.getFirst();
        if (!Objects.equals(first.getType(), MsgTypeEnum.reply)) {
            return null;
        }
        JsonNode data = first.getData();
        if (!Objects.equals(first.getType(), MsgTypeEnum.reply)) {
            return null;
        }
        String messageIdStr = data.get("id").asString();
        int messageId = Integer.parseInt(messageIdStr);
        ActionData<MsgResp> msg = bot.getMsg(messageId);
        MsgResp msgRespData = msg.getData();
        return msgRespData.getUserId();
    }

    public static boolean isReply(AnyMessageEvent event) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        if (arrayMsg.isEmpty()) {
            return false;
        }
        ArrayMsg first = arrayMsg.getFirst();
        return Objects.equals(first.getType(), MsgTypeEnum.reply);
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
            log.error("Download error: ", e);
            return null;
        }
    }

    public static ArrayMsg text(String text) {
        return ArrayMsgUtils.builder().text(text).build().getFirst();
    }

    public static List<ArrayMsg> simpText(String text) {
        return ArrayMsgUtils.builder().text(text).build();
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

    public static ArrayMsg voice(byte[] bytes) {
        TempFileController bean = SpringContextHolder.getBean(TempFileController.class);
        UUID audioId = bean.saveFile(bytes);
        return ArrayMsgUtils.builder().voice("http://127.0.0.1:9920/api/temp_file/get_voice/%s".formatted(audioId)).build().getFirst();
    }

    public static ArrayMsg at(Long userId) {
        return ArrayMsgUtils.builder().at(userId).build().getFirst();
    }

    public static ArrayMsg at(User user) {
        return ArrayMsgUtils.builder().at(user.getUserId()).build().getFirst();
    }

    public static int len(List<ArrayMsg> arrayMsgs) {
        StringBuilder sb = new StringBuilder();
        for (ArrayMsg item : arrayMsgs) {
            if (item.getType()==MsgTypeEnum.text) {
                JsonNode text = item.getData().get("text");
                if (text == null) {
                    continue;
                }
                if (text.isString()) {
                    sb.append(text.asString());
                }
            } else {
                sb.append("@");
            }
        }
        return sb.length();
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

    public static boolean contains(List<ArrayMsg> arrayMsgs, String str) {
        Stream<String> arrayMsgStream = arrayMsgs.stream()
                .filter(item -> item.getType() == MsgTypeEnum.text)
                .map(MsgTool::_getText);
        List<String> list = arrayMsgStream.toList();
        for (String s : list) {
            if (s.contains(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWith(List<ArrayMsg> arrayMsgs, String str) {
        Stream<String> arrayMsgStream = arrayMsgs.stream()
                .filter(item -> item.getType() == MsgTypeEnum.text)
                .map(MsgTool::_getText);
        List<String> list = arrayMsgStream.toList();
        if (list.isEmpty()) {
            return false;
        }
        String first = list.getFirst();
        return first.startsWith(str);
    }

    public static boolean endsWith(List<ArrayMsg> arrayMsgs, String str) {
        Stream<String> arrayMsgStream = arrayMsgs.stream()
                .filter(item -> item.getType() == MsgTypeEnum.text)
                .map(MsgTool::_getText);
        List<String> list = arrayMsgStream.toList();
        if (list.isEmpty()) {
            return false;
        }
        String last = list.getLast();
        return last.endsWith(str);
    }

    public static String _getText(ArrayMsg arrayMsg) {
        if (!(arrayMsg.getType() == MsgTypeEnum.text)) {
            return null;
        }
        JsonNode data = arrayMsg.getData();
        JsonNode text = data.get("text");
        if (text == null) {
            return null;
        }
        return text.asString();
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

    public static String filterText(List<ArrayMsg> list) {
        StringBuilder sb = new StringBuilder();
        list.stream().filter(item -> item.getType() == MsgTypeEnum.text)
                .forEach(item -> {
                    JsonNode text = item.getData().get("text");
                    if (text == null) {
                        return;
                    }
                    if (text.isString()) {
                        sb.append(text.asString());
                    }
                });
        return sb.toString();
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

    public static String toString(List<ArrayMsg> arrayMsg) {
        StringBuilder sb = new StringBuilder();
        Stream<ArrayMsg> arrayMsgStream = arrayMsg.stream().filter(item -> item.getType() == MsgTypeEnum.text);
        arrayMsgStream.forEach(item -> {
            JsonNode textNode = item.getData().get("text");
            if (textNode.isString()) {
                sb.append(textNode.asString());
            }
        });
        return sb.toString();
    }
}
