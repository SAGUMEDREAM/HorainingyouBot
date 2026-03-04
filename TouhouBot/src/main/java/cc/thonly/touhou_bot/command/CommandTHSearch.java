package cc.thonly.touhou_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.JsonData;
import cc.thonly.touhou_bot.data.THSearchResult;
import cc.thonly.touhou_bot.sheet.SheetYears;
import cc.thonly.touhou_bot.view.kv.THSearchView;
import cc.thonly.horainingyoubot.repository.JsonDataRepository;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.*;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.*;

@Slf4j
@Command
public class CommandTHSearch implements CommandEntrypoint {
    private static final long CACHE_DURATION = 12 * 60 * 60 * 1000L;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    JsonDataRepository jsonDataRepository;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("活动搜索")
                        .withAliasName("搜索活动")
                        .withArguments("#{keyword} #{history_mode} #{reload}")
                        .withDefaultArgument("history_mode", false)
                        .withDefaultArgument("reload", false)
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            boolean historyMode = args.getBoolean("history_mode");
                            boolean reload = args.getBoolean("reload");

                            boolean needSave = false;

                            JsonData jsonData = jsonDataRepository
                                    .findById(JsonDataRepository.KEY_TH_SEARCHER)
                                    .orElseGet(() -> {
                                        JsonData jd = new JsonData();
                                        jd.setId(JsonDataRepository.KEY_TH_SEARCHER);
                                        jd.setInternalElement(new JsonObject());
                                        return jd;
                                    });

                            THSearchView view = jsonData.getView(THSearchView::new);
                            bot.sendMsg(event, "正在搜索中...", false);

                            long now = System.currentTimeMillis();

                            // 强制重载
                            if (reload) {
                                view.setTimestamp(0);
                                view.setResult(Collections.emptyList());
                                needSave = true;
                            }

                            List<THSearchResult> results;

                            // 使用缓存
                            if (now - view.getTimestamp() < CACHE_DURATION) {
                                results = view.getResults();
                            }
                            // 刷新缓存
                            else {
                                results = fetchAllResults();
                                view.setTimestamp(now);
                                view.setResult(results);
                                needSave = true;
                            }

                            // 保存数据库
                            if (needSave) {
                                jsonDataRepository.save(jsonData);
                            }

                            sendSearchResults(bot, event, results, keyword, historyMode);
                        })
        );
    }

    private List<THSearchResult> fetchAllResults() {
        long now = System.currentTimeMillis();

        List<THSearchResult> all = new ArrayList<>();

        for (String url : SheetYears.THONLY_SHEETS_API) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        HTTP.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                all.addAll(parseApiData(json, now));

            } catch (Exception e) {
                log.error("THSearch API error: ", e);
            }
        }

        return all;
    }

    private List<THSearchResult> parseApiData(JsonObject data, long nowTimestamp) {

        List<THSearchResult> results = new ArrayList<>();

        JsonArray ranges = data.has("valueRanges")
                ? data.getAsJsonArray("valueRanges")
                : new JsonArray();

        for (JsonElement rangeEl : ranges) {
            JsonObject range = rangeEl.getAsJsonObject();
            JsonArray values = range.has("values")
                    ? range.getAsJsonArray("values")
                    : new JsonArray();

            for (JsonElement rowEl : values) {
                JsonArray item = rowEl.getAsJsonArray();
                if (item.size() < 5) continue;

                try {
                    String status = item.get(0).getAsString();
                    String name = item.get(1).getAsString();
                    String area = item.get(2).getAsString();
                    String time = item.get(3).getAsString();
                    String group = item.get(4).getAsString();

                    long ts = ("暂无".equals(time) || time == null)
                            ? nowTimestamp + 8000
                            : parseTime(time);

                    if ("取消".equals(status)) {
                        name += "(已取消)";
                    }

                    results.add(new THSearchResult(
                            status, name, area, time, group, ts
                    ));

                } catch (Exception e) {
                    log.error("Parse error: ", e);
                }
            }
        }

        return results;
    }

    private void sendSearchResults(Bot bot,
                                   AnyMessageEvent event,
                                   List<THSearchResult> results,
                                   String keyword,
                                   boolean historyMode) {

        long now = System.currentTimeMillis();

        List<THSearchResult> filtered = results.stream()
                .filter(r ->
                        contains(r.getName(), keyword) ||
                                contains(r.getGroupId(), keyword) ||
                                contains(r.getArea(), keyword) ||
                                contains(r.getTime(), keyword)
                )
                .filter(r ->
                        "暂无".equals(r.getTime()) ||
                                r.getTimestamp() > now ||
                                historyMode
                )
                .sorted(Comparator.comparingLong(THSearchResult::getTimestamp))
                .toList();

        if (filtered.isEmpty()) {
            bot.sendMsg(event, "❗没有找到与 " + keyword + " 相关的活动。", false);
            return;
        }

        String header = ">>>" + keyword +
                " 的搜索结果如下:\n" +
                "✨共找到 " + filtered.size() + " 个结果。\n";

        List<String> messages = new ArrayList<>();
        messages.add(header);
        for (THSearchResult r : filtered) {
            String sb = "名称: " + r.getName() + "\n" +
                    "地区: " + r.getArea() + "\n" +
                    "日期: " + r.getTime() + "\n" +
                    "群号: " + r.getGroupId() + "\n";
            messages.add(sb);
        }
        List<Map<String, Object>> messageMerging = MsgTool.createForwardMsg(bot.getSelfId(), "蓬莱人形Bot", messages);
        bot.sendForwardMsg(event, messageMerging);
    }

    @SuppressWarnings("deprecation")
    private long parseTime(String time) {
        try {
            return new Date(time).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean contains(String text, String keyword) {
        if (text == null || keyword == null) return false;
        return text.toLowerCase().contains(keyword.toLowerCase());
    }
}
