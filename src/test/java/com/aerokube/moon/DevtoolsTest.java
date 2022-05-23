package com.aerokube.moon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.devtools.v99.log.Log;
import org.openqa.selenium.devtools.v99.runtime.Runtime;
import org.openqa.selenium.devtools.v99.network.Network;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Optional.empty;
import static org.openqa.selenium.remote.http.Contents.utf8String;

class DevtoolsTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() throws Exception {
        ChromeOptions capabilities = new ChromeOptions();
        capabilities.setBrowserVersion("99.0");
        driver = new RemoteWebDriver(new URL("https://username:password@my-company.cloud.aerokube.com/wd/hub"), capabilities);
        driver = new Augmenter().augment(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    void testPrintNetworkRequests() {
        try (DevTools devTools = ((HasDevTools) driver).getDevTools()) {
            devTools.createSession();

            devTools.send(Network.enable(empty(), empty(), empty()));

            devTools.addListener(Network.requestWillBeSent(), r -> System.out.println(r.getRequest().getUrl()));

            driver.get("https://aerokube.com/");
        }
    }

    @Test
    void testMockNetworkRequests() throws IOException {

        NetworkInterceptor interceptor = new NetworkInterceptor(
                driver,
                Route.matching(req -> true)
                        .to(() -> req -> new HttpResponse()
                                .setStatus(200)
                                .addHeader("Content-Type", "text/html; charset=utf-8")
                                .setContent(utf8String("<html><body><h1>Text</h1></body></html>"))));

        driver.get("https://aerokube.com/");
        Path screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
        Files.copy(screenshot, Paths.get("test-mocked-response.png"));
    }

    @Test
    void testPrintConsoleLogs() {
        try (DevTools devTools = ((HasDevTools) driver).getDevTools()) {
            devTools.createSession();

            devTools.send(Runtime.enable());

            devTools.addListener(Log.entryAdded(), e -> System.out.printf("%s %s", e.getLevel().toString(), e.getText()));

            driver.get("https://aerokube.com/");
        }
    }
}
