package com.aerokube.moon;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

class MoonCloudExampleTest {

    private RemoteWebDriver driver;

    @BeforeEach
    void setUp() throws Exception {
        ChromeOptions capabilities = new ChromeOptions();
        capabilities.setCapability("browserVersion", "92.0");
//        capabilities.setCapability("moon:options", Map.of(
//                "enableVideo", true,
//                "enableVNC", true,
//                "name", "MyCoolTest",
//                "screenResolution", "1024x768",
//                "sessionTimeout", "3m"
//        ));
        driver = new RemoteWebDriver(new URL("https://username:password@my-company.cloud.aerokube.com/wd/hub"), capabilities);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    void testTakeScreenshot() throws IOException {
        driver.get("https://aerokube.com/");
        Path screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
        Files.copy(screenshot, Paths.get(driver.getSessionId() + ".png"));
    }
}
