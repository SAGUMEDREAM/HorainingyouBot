package cc.thonly.horainingyoubot.plugin.essential_bot.command;

import cc.thonly.horainingyoubot.browser.PlaywrightManager;
import cc.thonly.horainingyoubot.command.Command;
import cc.thonly.horainingyoubot.command.CommandEntrypoint;
import cc.thonly.horainingyoubot.command.CommandNode;
import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.service.AssetsManagerImpl;
import cc.thonly.horainingyoubot.service.StaticServiceImpl;
import cc.thonly.horainingyoubot.util.MsgTool;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

@Command
public class CommandXibao implements CommandEntrypoint {

    @Autowired
    StaticServiceImpl staticService;

    @Autowired
    PlaywrightManager playwrightManager;

    private byte[] data = null;

    @Override
    public void registerCommand(Commands commands) {
        commands.registerCommand(
                CommandNode.createRoot("喜报")
                        .withArguments("#{text}")
                        .withExecutor((bot, event, args) -> {
                            String text = args.getString("text");
                            if (text == null) {
                                return;
                            }
                            if (this.data == null) {
                                this.data = this.staticService.getAsByteArray("assets/xibao.jpg");
                            }
                            if (this.data == null) {
                                return;
                            }

                            String html = buildHtml(text, this.data);

                            Page page = this.playwrightManager.newPage();
                            try {
                                page.setContent(html);
                                page.waitForLoadState(LoadState.LOAD);
                                page.evaluate("""
                                        const dom = document.querySelector('body')
                                            const div = dom.querySelector('div')
                                            let fontSize = 80
                                            dom.style.fontSize = fontSize + 'px'
                                            while (div.offsetWidth >= 900 && fontSize > 38) {
                                              dom.style.fontSize = --fontSize + 'px'
                                            }
                                        """);
                                byte[] screenshot = page.locator("body")
                                        .screenshot(new Locator.ScreenshotOptions()
                                                .setTimeout(15_000));
//                                System.out.println(html);
                                MsgTool.reply(bot, event, MsgTool.img(screenshot));
                            } finally {
                                page.context().close();
                            }
                        })
        );
    }

    private String buildHtml(String text, byte[] imgData) {
        String base64Img = Base64.getEncoder().encodeToString(imgData);
        // 样式可按需修改
        String fontFamily = "\"HarmonyOS Sans SC\", \"Source Han Sans CN\", sans-serif";
        String fontColor = "#ff0a0a";
        String strokeColor = "#ffde00";
        int maxFontSize = 120;
        int minFontSize = 76;
        int offsetWidth = 900;
        String importCSS = "https://gitee.com/ifrank/harmonyos-fonts/raw/main/css/harmonyos_sans_sc.css";

        // HTML 模板
        return """
                <head>
                  <style>
                    @import url('%s');
                    body {
                      width: 960px;
                      height: 768px;
                      padding: 0 32px;
                      display: flex;
                      flex-direction: column;
                      justify-content: center;
                      align-items: center;
                      text-align: center;
                      margin: 0;
                      font-weight: 900;
                      font-family: %s;
                      color: %s;
                      -webkit-text-stroke: 2.5px %s;
                      background-image: url(data:image/png;base64,%s);
                      background-repeat: no-repeat;
                    }
                  </style>
                </head>
                <body>
                  <div>%s</div>
                </body>
                <script>
                  const dom = document.querySelector('body');
                  const div = dom.querySelector('div');
                  let fontSize = %d;
                  dom.style.fontSize = fontSize + 'px';
                  while(div.offsetWidth >= %d && fontSize > %d) {
                    dom.style.fontSize = --fontSize + 'px';
                  }
                </script>
                """.formatted(importCSS, fontFamily, fontColor, strokeColor, base64Img, escapeHtml(text),
                maxFontSize, offsetWidth, minFontSize);
    }

    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("\n", "<br/>");
    }
}