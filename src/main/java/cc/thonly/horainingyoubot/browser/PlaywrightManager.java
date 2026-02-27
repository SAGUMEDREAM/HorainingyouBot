package cc.thonly.horainingyoubot.browser;

import com.microsoft.playwright.options.Proxy;
import org.springframework.stereotype.Component;
import com.microsoft.playwright.*;
import jakarta.annotation.PreDestroy;

@Component
public class PlaywrightManager {
    private final Playwright playwright;
    private final Browser browser;

    public PlaywrightManager() {
        this.playwright = Playwright.create();

        this.browser = this.playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
        );
    }

    public Page newPage() {
        BrowserContext context = this.browser.newContext(
                new Browser.NewContextOptions()
                        .setDeviceScaleFactor(2.5)
                        .setJavaScriptEnabled(false)
        );
        return context.newPage();
    }

    public Page newPageWithProxy(Proxy proxy) {
        BrowserContext context = this.browser.newContext(
                new Browser.NewContextOptions()
                        .setProxy(proxy)
                        .setDeviceScaleFactor(2.5)
                        .setJavaScriptEnabled(false)
        );
        return context.newPage();
    }

    @PreDestroy
    public void close() {
        this.browser.close();
        this.playwright.close();
    }
}
