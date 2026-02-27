package cc.thonly.horainingyoubot.browser;


import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarkdownImageFactory {

    @Autowired
    PlaywrightManager manager;

    public MarkdownImage render(List<String> markdowns) {
        return this.render(String.join("\n", markdowns));
    }

    public MarkdownImage render(String markdownText) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        Node document = parser.parse(markdownText);
        String html = renderer.render(document);

        String css = CSSTools.tryGetGithubMarkdownCSS();

        html = """
                <html>
                <head>
                <style>%s</style>
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
                </head>
                <body class="markdown-body">
                %s
                </body>
                </html>
                """.formatted(css, html);

        byte[] bytes;

        Page page = this.manager.newPage();
        try {
            page.setDefaultTimeout(15_000);
            page.setContent(html);

            bytes = page.locator("body")
                    .screenshot(new Locator.ScreenshotOptions()
                            .setTimeout(15_000));
        } finally {
            page.context().close();
        }

        return new MarkdownImage(markdownText, bytes);
    }
}