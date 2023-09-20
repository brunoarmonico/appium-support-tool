package core.test_support;

import core.thread.LocalCucumber;
import core.thread.LocalDevice;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Appium Screenshot
 */
public class ScreenCapture {

    /**
     * Take Screenshot based on properties options
     */
    public void takeScreenshot() {
        Scenario scenario = LocalCucumber.getScenario();
        AppiumDriver driver = LocalDevice.getDriver();
        String assertOption = (String) System.getProperties().get("framework.screenshot-evidence.assertion");
        if ((scenario.isFailed() && (assertOption.equalsIgnoreCase("all") || assertOption.equalsIgnoreCase("fail")))
                ||
                (!scenario.isFailed() && (assertOption.equalsIgnoreCase("all") || assertOption.equalsIgnoreCase("pass")))) {

            final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot");
        }
    }
}
