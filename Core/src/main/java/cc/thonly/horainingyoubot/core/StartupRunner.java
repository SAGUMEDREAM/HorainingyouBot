package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.Constants;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.config.BotProperties;
import cc.thonly.horainingyoubot.controller.TempFileController;
import cc.thonly.horainingyoubot.event.internal.EventBusFactory;
import cc.thonly.horainingyoubot.browser.CSSTools;
import cc.thonly.horainingyoubot.service.DataManagerImpl;
import cc.thonly.horainingyoubot.util.DeferTask;
import com.microsoft.playwright.*;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired
    TempFileController tempFileController;
    @Autowired
    Commands commands;
    @Autowired
    JPluginLoader jPluginLoader;
    @Autowired
    InternalCommands internalCommands;
    @Autowired
    BotProperties botProperties;
    @Autowired
    DataManagerImpl dataManager;

    @SneakyThrows
    @Override
    public void run(String @NonNull ... args) {
        if (Constants.CACHED_SELF_ID == null || Constants.CACHED_SELF_ID.isEmpty()) {
            Constants.CACHED_SELF_ID = this.botProperties.getSelfId().toString().toLowerCase().replace("L", "");
        }
        try (Playwright playwright = Playwright.create()) {
            playwright.chromium();
        }
        List<Path> paths = List.of(
                Path.of("./assets"),
                Path.of("./data"),
                Path.of("./temp")
        );
        for (Path path : paths) {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
        }
        if (!this.dataManager.exists("help.md")) {
            this.dataManager.save("help.md", "".getBytes(StandardCharsets.UTF_8));
        }
        DeferTask.TASKS.clear();
        this.tempFileController.clear();
        EventBusFactory.clear();
        this.commands.drops();
        this.internalCommands.accept(this.commands);
        this.jPluginLoader.unloadPlugins();
        this.jPluginLoader.loadPlugins();
        Map<String, CommandNode> root2Node = this.commands.getRoot2Node();
        Map<String, CommandNode> sortedMap = root2Node.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        this.commands.modify(sortedMap);
    }

    void test() {
        CommandNode root = CommandNode.createRoot("user")
                .addNode("create-value").withArguments("#{a} #{b}")
                .withExecutor((bot, event, args) -> {
                    bot.sendMsg(event, "你的输入参数: %s, %s".formatted(args.getString("a"), args.getString("b")), false);
                }).up();
        this.commands.registerCommand(root);
    }

    void test2() {
        CommandNode root = CommandNode.createRoot("md-test")
                .addNode("create-from-text")
                .withArguments("#{text}")
                .withExecutor((bot, event, args) -> {
                    String markdownText = args.getString("text");
                    if (markdownText == null) {
                        return;
                    }

                    Parser parser = Parser.builder().build();
                    HtmlRenderer renderer = HtmlRenderer.builder().build();

                    Node document = parser.parse(markdownText);
                    String html = renderer.render(document);
                    String css = CSSTools.tryGetGithubMarkdownCSS();
                    html = """
                            <html>
                            <head>
                            <style>
                            body {
                              margin: 0;
                              padding: 16px;
                              display: inline-block;
                            }
                            body {
                              margin: 0;
                              padding: 24px;
                              background: white;
                            }
                            .markdown-body {
                            		box-sizing: border-box;
                            		min-width: 200px;
                            		max-width: 980px;
                            		margin: 0 auto;
                            		padding: 45px;
                            	}
                            </style>
                            <style>
                            %s
                            </style>
                            </head>
                            <body class="markdown-body">
                            %s
                            </body>
                            </html>
                            """.formatted(css, html);
                    byte[] bytes;
                    try (Playwright playwright = Playwright.create();
                         Browser browser = playwright.chromium().launch()) {

                        Page page = browser.newPage();

                        page.setContent(html);
                        page.waitForLoadState();
                        page.setDefaultTimeout(15_000);
                        page.setDefaultNavigationTimeout(15_000);

                        bytes = page.locator("body").screenshot(new Locator.ScreenshotOptions().setTimeout(15_000));
                    }
                    bot.sendMsg(event, ArrayMsgUtils.builder().text("你的输入参数: %s".formatted(markdownText)).img(bytes).build(), false);

                }).up();
        this.commands.registerCommand(root);
    }
}
