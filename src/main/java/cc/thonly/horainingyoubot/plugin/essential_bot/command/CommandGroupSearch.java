package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.data.db.JsonData;
import cc.thonly.horainingyoubot.plugin.essential_bot.data.GroupSearchCache;
import cc.thonly.horainingyoubot.plugin.essential_bot.view.kv.GroupSearchView;
import cc.thonly.horainingyoubot.repository.JsonDataRepository;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Command
@SuppressWarnings("RegExpRedundantEscape")
public class CommandGroupSearch implements CommandEntrypoint {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String API =
            "https://thwiki.cc/api.php?action=parse&page=%E4%B8%9C%E6%96%B9%E7%9B%B8%E5%85%B3QQ%E7%BE%A4%E7%BB%84%E5%88%97%E8%A1%A8&prop=wikitext&format=json";

    private static final Pattern QQ_REGEX =
            Pattern.compile("\\{\\{(QQ群|QQ群扩展)\\|(.+?)\\|(.+?)\\|(\\d+?)\\|(.+?)\\}\\}");

    private static final Pattern CLEAN_REGEX =
            Pattern.compile("\\|[^|]+");


    private static final Pattern CLEAN_EVENT_REGEX =
            Pattern.compile("\\[\\[.*?\\]\\]");

    @Autowired
    JsonDataRepository jsonDataRepository;

    private static final long CACHE_DURATION = 12L * 60 * 60 * 1000 * 14;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("搜索群组")
                        .withAliasName("群组搜索")
                        .withArguments("#{keyword} #{reload}")
                        .withDefaultArgument("reload", false)
                        .withExecutor((bot, event, args) -> {

                            String keyword = args.getString("keyword");
                            boolean reload = args.getBoolean("reload");

                            bot.sendMsg(event, "正在搜索中...", false);

                            JsonData jsonData = jsonDataRepository
                                    .findById(JsonDataRepository.KEY_TH_GROUP)
                                    .orElseGet(() -> {
                                        JsonData jd = new JsonData();
                                        jd.setId(JsonDataRepository.KEY_TH_GROUP);
                                        jd.setInternalElement(new JsonObject());
                                        return jd;
                                    });

                            GroupSearchView view = jsonData.getView(GroupSearchView::new);

                            List<GroupSearchCache.Item> data;
                            long now = System.currentTimeMillis();

                            if (!reload && now - view.getTimestamp() < CACHE_DURATION) {
                                data = view.getData();
                            } else {
                                data = fetchGroups();
                                view.setData(data);
                                view.setTimestamp(now);
                                jsonDataRepository.save(jsonData);
                            }

                            sendResults(bot, event, data, keyword);
                        })
        );
    }

    private void sendResults(Bot bot,
                             AnyMessageEvent event,
                             List<GroupSearchCache.Item> data,
                             String keyword) {

        List<GroupSearchCache.Item> filtered = data.stream()
                .filter(g ->
                        contains(g.getGroupName(), keyword) ||
                                contains(g.getGroupId(), keyword) ||
                                contains(g.getEventName(), keyword) ||
                                contains(g.getCity(), keyword)
                )
                .toList();

        if (filtered.isEmpty()) {
            bot.sendMsg(event, "没有找到符合条件的群组😥", false);
            return;
        }

        List<String> messages = new ArrayList<>();
        messages.add(">>>" + keyword + " 的搜索结果如下:");

        StringBuilder block = new StringBuilder();
        int count = 0;

        for (int i = 0; i < filtered.size(); i++) {
            GroupSearchCache.Item g = filtered.get(i);

            block.append("群名称: ").append(g.getGroupName()).append("\n")
                    .append("所属机构: ").append(g.getEventName()).append("\n")
                    .append("群号: ").append(g.getGroupId()).append("\n");

            count++;

            if (count < 4 && i != filtered.size() - 1) {
                block.append("————————\n");
            }

            if (count == 4 || i == filtered.size() - 1) {
                messages.add(block.toString());
                block.setLength(0);
                count = 0;
            }
        }

        messages.add("数据来源: https://touhou.group/");

        List<Map<String, Object>> forward =
                MsgTool.createForwardMsg(bot.getSelfId(), "蓬莱人形Bot", messages);

        bot.sendForwardMsg(event, forward);
    }

    private List<GroupSearchCache.Item> fetchGroups() {
        List<GroupSearchCache.Item> list = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HTTP.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            String text = json
                    .getAsJsonObject("parse")
                    .getAsJsonObject("wikitext")
                    .get("*")
                    .getAsString();

            String[] lines = text.split("\n");

            for (String line : lines) {
                Matcher m = QQ_REGEX.matcher(line);
                while (m.find()) {

                    String city = m.group(2);

                    String eventName = CLEAN_EVENT_REGEX
                            .matcher(m.group(3))
                            .replaceAll("")
                            .trim();

                    String groupId = m.group(4);

                    String groupName = CLEAN_REGEX
                            .matcher(m.group(5))
                            .replaceAll("")
                            .trim();

                    String type = "QQ群".equals(m.group(1)) ? "普通" : "扩展";

                    list.add(new GroupSearchCache.Item(
                            city, groupName, eventName, groupId, type
                    ));
                }
            }

        } catch (Exception e) {
            log.error("GroupSearch API error", e);
        }

        return list;
    }

    private boolean contains(String text, String keyword) {
        if (text == null || keyword == null) return false;
        return text.toLowerCase().contains(keyword.toLowerCase());
    }
}
