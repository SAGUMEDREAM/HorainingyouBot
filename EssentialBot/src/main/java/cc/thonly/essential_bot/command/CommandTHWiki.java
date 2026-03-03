package cc.thonly.essential_bot.command;

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
public class CommandTHWiki implements CommandEntrypoint {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("THBWiki维基搜索")
                        .withAliasName("thb搜索")
                        .withAliasName("THB搜索")
                        .withAliasName("东方百科")
                        .withAliasName("thb")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            this.search(bot, event, keyword);
                        })
        );
    }

    private void search(Bot bot, AnyMessageEvent event, String keyword) {
        bot.sendMsg(event, "正在搜索中...", false);

        try {
            String endpoint =
                    "https://thwiki.cc/api.php?action=query&list=search&format=json&srlimit=6&srsearch=" +
                            URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HTTP.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject query = root.getAsJsonObject("query");

            if (query == null) {
                bot.sendMsg(event, "没有找到结果", false);
                return;
            }

            int totalHits = query
                    .getAsJsonObject("searchinfo")
                    .get("totalhits")
                    .getAsInt();

            JsonArray search = query.getAsJsonArray("search");

            List<String> messages = new ArrayList<>();

            messages.add(
                    "THBWiki搜索关键词: " + keyword + "\n" +
                            "关键词匹配总数: " + totalHits + "\n" +
                            "展示 1~6 条结果\n"
            );

            for (JsonElement el : search) {
                JsonObject item = el.getAsJsonObject();

                String title = item.get("title").getAsString();
                String timestamp = item.get("timestamp").getAsString();
                String snippet = cleanSnippet(item.get("snippet").getAsString());

                String text =
                        "标题: " + title + "\n" +
                                "链接: https://thwiki.cc/" +
                                URLEncoder.encode(title, StandardCharsets.UTF_8) + "\n" +
                                "时间: " + timestamp + "\n" +
                                "简介: " + snippet + "\n";

                messages.add(text);
            }

            messages.add(
                    "更多结果:\nhttps://thwiki.cc/index.php?search=" +
                            URLEncoder.encode(keyword, StandardCharsets.UTF_8)
            );

            List<Map<String, Object>> forward =
                    MsgTool.createForwardMsg(bot.getSelfId(), "蓬莱人形Bot", messages);

            bot.sendForwardMsg(event, forward);

        } catch (Exception e) {
            log.error("THBWiki search error", e);
            bot.sendMsg(event, "获取失败，请稍后再试", false);
        }
    }

    private String cleanSnippet(String snippet) {
        if (snippet == null) return "";

        // 去HTML标签
        String noHtml = snippet.replaceAll("</?[^>]+>", "");

        // 限长
        int maxLength = 200;
        if (noHtml.length() > maxLength) {
            return noHtml.substring(0, maxLength) + "...";
        }

        return noHtml;
    }
}