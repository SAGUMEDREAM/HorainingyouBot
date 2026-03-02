package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Command
public class CommandAbbreviation implements CommandEntrypoint {
    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("何意味")
                        .withArguments("#{text}")
                        .bypassEulaCheck()
                        .withExecutor((bot, event, args) -> {
                            String text = args.getString("text");
                            if (text == null || text.isBlank()) {
                                return;
                            }

                            try {
                                // 构造请求体
                                JsonObject requestBody = new JsonObject();
                                requestBody.addProperty("text", text);

                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create("https://lab.magiconch.com/api/nbnhhsh/guess"))
                                        .header("Content-Type", "application/json")
                                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                                        .build();

                                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

                                JsonArray data = GSON.fromJson(response.body(), JsonArray.class);
                                if (data == null || data.isEmpty()) {
                                    MsgTool.reply(bot, event, "何意味我也不知道啊");
                                    return;
                                }

                                JsonArray array = GSON.fromJson(response.body(), JsonArray.class);
                                StringBuilder sb = new StringBuilder();

                                for (JsonElement elem : array) {
                                    JsonObject entry = elem.getAsJsonObject();
                                    String line = formatEntry(entry);
                                    if (line != null && !line.isBlank()) {
                                        if (!sb.isEmpty()) sb.append("\n");
                                        sb.append(line);
                                    }
                                }

                                String result = sb.toString();
                                if (result.isBlank()) result = "何意味我也不知道啊";

                                if (result.isBlank()) result = "何意味我也不知道啊";

                                MsgTool.reply(bot, event, result);

                            } catch (Exception e) {
                                MsgTool.reply(bot, event, "何意味我也不造啊?");
                            }
                        })
        );

    }

    private String formatEntry(JsonObject entry) {
        String name = entry.has("name") ? entry.get("name").getAsString() : "";
        String meaning = getResult(entry);

        if (containsBannedWords(name) || containsBannedWords(meaning)) return null;
        return name + "：" + meaning;
    }

    private String getResult(JsonObject entry) {
        if (entry.has("trans") && entry.get("trans").isJsonArray()) {
            return joinArray(entry.getAsJsonArray("trans"));
        }
        if (entry.has("inputting") && entry.get("inputting").isJsonArray()) {
            return joinArray(entry.getAsJsonArray("inputting"));
        }
        return "未找到对应的缩写。";
    }

    private String joinArray(JsonArray arr) {
        StringBuilder sb = new StringBuilder();
        for (JsonElement elem : arr) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(elem.getAsString());
        }
        return sb.toString();
    }

    private boolean containsBannedWords(String text) {
        if (text == null) return false;
        return BANNED_WORDS.stream().anyMatch(text::contains);
    }


    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    private static final List<String> BANNED_WORDS = List.of(
            "支那", "水晶棺", "王沪宁", "周恩来", "法轮功", "共产党", "64", "89", "六四",
            "八九", "习近平", "江泽民", "台独", "8964", "天安门", "白纸", "老毛", "腊肉"
    );
}