package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.google.gson.*;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Slf4j
@Command
public class CommandLilySearch implements CommandEntrypoint {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("lily")
                        .withAliasName("莉莉云")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            search(bot, event, keyword);
                        })
        );
    }

    private void search(Bot bot, AnyMessageEvent event, String keyword) {
        bot.sendMsg(event, "正在搜索中...", false);

        try {
            String apiUrl = "https://cn.thdog.moe/api/fs/search";

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("parent", "/分流1");
            requestBody.addProperty("keywords", keyword);
            requestBody.addProperty("scope", 0);
            requestBody.addProperty("page", 1);
            requestBody.addProperty("per_page", 100);
            requestBody.addProperty("password", "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response =
                    HTTP.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            int code = root.get("code").getAsInt();
            if (code != 200) {
                bot.sendMsg(event, "请求失败，请稍后再试。", false);
                return;
            }

            JsonObject data = root.getAsJsonObject("data");
            JsonArray content = data.getAsJsonArray("content");

            if (content.size() == 0) {
                bot.sendMsg(event, "没有找到与【" + keyword + "】相关的文件。", false);
                return;
            }

            List<String> messages = new ArrayList<>();

            messages.add(
                    ">>>" + keyword + " 的搜索结果如下:\n" +
                            "✨共找到 " + content.size() + " 个结果。\n"
            );

            for (JsonElement el : content) {
                JsonObject file = el.getAsJsonObject();

                String name = file.get("name").getAsString();
                String parent = file.get("parent").getAsString();

                String url =
                        "https://cn.thdog.moe/" +
                                encodePath(parent) + "/" +
                                encodePath(name);

                String text =
                        "文件名: " + name + "\n" +
                                "所属目录: " + parent + "\n" +
                                "下载链接: " + url + "\n";

                messages.add(text);
            }

            messages.add("\n数据来源: https://touhou.group/");

            List<Map<String, Object>> forward =
                    MsgTool.createForwardMsg(bot.getSelfId(), "蓬莱人形Bot", messages);

            bot.sendForwardMsg(event, forward);

        } catch (Exception e) {
            log.error("Lily search error", e);
            bot.sendMsg(event, "❌请求失败，请稍后再试。", false);
        }
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}