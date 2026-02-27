package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.Constants;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.config.BotProperties;
import cc.thonly.horainingyoubot.event.internal.EventBusFactory;
import cc.thonly.horainingyoubot.plugin.JPluginLoader;
import cc.thonly.horainingyoubot.browser.CSSTools;
import com.microsoft.playwright.*;
import com.mikuac.shiro.common.utils.ArrayMsgUtils;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired
    Commands commands;
    @Autowired
    JPluginLoader jPluginLoader;
    @Autowired
    InternalCommands internalCommands;
    @Autowired
    BotProperties botProperties;

    @Override
    public void run(String @NonNull ... args) {
        if (Constants.CACHED_SELF_ID == null || Constants.CACHED_SELF_ID.isEmpty()) {
            Constants.CACHED_SELF_ID = this.botProperties.getSelfId().toString().toLowerCase().replace("L", "");
        }
        try (Playwright playwright = Playwright.create()) {
            playwright.chromium();
        }
        EventBusFactory.clear();
        this.commands.drops();
        this.internalCommands.accept(this.commands);
        this.jPluginLoader.unloadPlugins();
        this.jPluginLoader.loadPlugins();
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
