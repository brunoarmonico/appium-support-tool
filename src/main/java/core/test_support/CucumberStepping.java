package core.test_support;

import core.thread.LocalDevice;
import io.appium.java_client.AppiumDriver;

/**
 * Cucumber After or Before test utilities
 */
public class CucumberStepping {

    public void afterTestConditions() {
        AppiumDriver driver = LocalDevice.getDriver();
        if (driver != null) {
            new ScreenRecording().stopRecording();
            new ScreenCapture().takeScreenshot();
            driver.quit();
        } else {
            System.out.println("There is no Appium Driver instance");
        }
    }

}
