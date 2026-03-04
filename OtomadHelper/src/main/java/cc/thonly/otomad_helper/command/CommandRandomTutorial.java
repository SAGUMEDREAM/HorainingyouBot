package cc.thonly.otomad_helper.command;

import cc.thonly.horainingyoubot.browser.PlaywrightManager;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.ScreenshotType;
import com.microsoft.playwright.options.WaitUntilState;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Command
public class CommandRandomTutorial implements CommandEntrypoint {
    private static final String BASE_URL = "https://otomad.wiki";
    private static final String TARGET_URL = BASE_URL + "/%E5%88%B6%E4%BD%9C%E6%95%99%E7%A8%8B";

    @Autowired
    PlaywrightManager playwrightManager;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("随机教程")
                        .withAliasName("随机音mad教程")
                        .withExecutor((bot, event, args) -> {
                            try (HttpClient client = HttpClient.newHttpClient()) {

                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(TARGET_URL))
                                        .GET()
                                        .build();

                                HttpResponse<String> response =
                                        client.send(request, HttpResponse.BodyHandlers.ofString());

                                String html = response.body();

                                Document doc = Jsoup.parse(html);
                                Elements elements = doc.select(".mw-body a");

                                List<String> links = new ArrayList<>();

                                for (Element element : elements) {
                                    String href = element.attr("href");
                                    if (!href.isEmpty() && !href.contains("#")) {
                                        links.add(href);
                                    }
                                }

                                if (links.isEmpty()) {
                                    MsgTool.reply(bot, event, "未找到链接");
                                    return;
                                }

                                String selected = links.get(new Random().nextInt(links.size()));

                                if (selected.startsWith("/")) {
                                    selected = BASE_URL + selected;
                                }
                                Page page = this.playwrightManager.newPage();
                                page.navigate(selected, new Page.NavigateOptions()
                                        .setWaitUntil(WaitUntilState.NETWORKIDLE));
                                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                                        .setType(ScreenshotType.PNG)
                                        .setFullPage(true));
                                page.close();
                                MsgTool.reply(bot, event, ArrayMsgUtils.builder().img(screenshot).text(selected).build());
                            } catch (Exception err) {
                                log.error("Error: ", err);
                                MsgTool.reply(bot, event, "请求失败");
                            }
                        })
        );

    }
}