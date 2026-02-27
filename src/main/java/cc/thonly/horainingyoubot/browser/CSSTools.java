package cc.thonly.horainingyoubot.browser;

import cc.thonly.horainingyoubot.HorainingyouBotApplication;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class CSSTools {
    private static String githubMarkdownCSS = null;

    public static String tryGetGithubMarkdownCSS() {
        if (githubMarkdownCSS == null) {
            try (
                    var stream = HorainingyouBotApplication.class.getResourceAsStream("/static/css/github-markdown.css");
            ) {
                if (stream == null) {
                    return null;
                }
                var bytes = stream.readAllBytes();
                githubMarkdownCSS = new String(
                        bytes,
                        StandardCharsets.UTF_8
                );
            } catch (Exception e) {
                log.error("Can't get css");
            }
        }
        return githubMarkdownCSS;
    }
}
