package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.browser.PlaywrightManager;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import com.microsoft.playwright.options.WaitUntilState;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.mikuac.shiro.common.utils.MsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Command
public class CommandOtmWiki implements CommandEntrypoint {
    private static final String API_ENDPOINT = "https://otomad.wiki/api.php?action=query&list=search&format=json&srlimit=8&srsearch=";

    @Autowired
    PlaywrightManager playwrightManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("音骂概念")
                        .withAliasName("音mad概念", "音MAD概念")
                        .withArguments("#{keyword}")
                        .withExecutor((bot, event, args) -> {
                            String keyword = args.getString("keyword");
                            if (keyword == null) {
                                return;
                            }

                            try (HttpClient httpClient = HttpClient.newHttpClient();) {
                                String url = API_ENDPOINT + java.net.URLEncoder.encode(keyword, "UTF-8");
                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(url))
                                        .GET()
                                        .build();

                                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                                JSONObject json = new JSONObject(response.body());
                                JSONObject query = json.getJSONObject("query");
                                JSONObject searchinfo = query.getJSONObject("searchinfo");
                                int totalHits = searchinfo.getInt("totalhits");
                                JSONArray searchArray = query.getJSONArray("search");

                                List<String> messages = new ArrayList<>();
                                messages.add("音MAD中文维基搜索关键词: " + keyword + "; 总匹配数: " + totalHits + "; 展示1~8搜索结果：");

                                for (int i = 0; i < searchArray.length(); i++) {
                                    JSONObject item = searchArray.getJSONObject(i);
                                    String title = item.getString("title");
                                    String snippet = cleanSnippet(item.getString("snippet"));
                                    String timestamp = item.getString("timestamp");
                                    String pageUrl = "https://otomad.wiki/" + java.net.URLEncoder.encode(title, "UTF-8");

                                    String text = String.format(
                                            "标题: %s\n链接: %s\n时间: %s\n简介: %s\n",
                                            title, pageUrl, timestamp, snippet
                                    );
//                                    Page page = this.playwrightManager.newPage();
//                                    page.navigate(pageUrl, new Page.NavigateOptions()
//                                            .setWaitUntil(WaitUntilState.NETWORKIDLE));
//                                    byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
//                                            .setType(ScreenshotType.PNG)
//                                            .setFullPage(true));
//                                    String img = MsgUtils.builder().img(screenshot).build();
//                                    page.close();
                                    messages.add(text
//                                            + img
                                    );
                                }
                                messages.add("更多结果：https://otomad.wiki/index.php?search=" + java.net.URLEncoder.encode(keyword, "UTF-8"));
                                bot.sendForwardMsg(event, MsgTool.createForwardMsg(bot.getSelfId(), "蓬莱人形Bot", messages));
                            } catch (Exception e) {
                                log.error("Error: ", e);
                                MsgTool.reply(bot, event, "获取失败");
                            }

                        })
        );

    }

    private String cleanSnippet(String snippet) {
        String withoutHtml = snippet.replaceAll("</?[^>]+(>|$)", "");

        Pattern pattern = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
        Matcher matcher = pattern.matcher(withoutHtml);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            int code = Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, Character.toString((char) code));
        }
        matcher.appendTail(sb);

        String decoded = sb.toString();

        int maxLength = 200;
        return decoded.length() > maxLength ? decoded.substring(0, maxLength) + "..." : decoded;
    }
}