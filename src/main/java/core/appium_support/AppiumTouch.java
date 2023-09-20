package core.appium_support;

import core.thread.LocalDevice;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

public class AppiumTouch {

    private AppiumDriver driver;

    public AppiumTouch(AppiumDriver driver) {
        this.driver = driver;
    }

    public AppiumTouch() {
        this.driver = LocalDevice.getDriver();
    }

    /**
     * Appium 8 W3C Touch Actions
     *
     * @param pointX Coordinate X Screen touch
     * @param pointY Coordinate Y Screen touch
     */
    public void tapCoordinates(int pointX, int pointY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence clickPosition = new Sequence(finger, 1);
        clickPosition.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), pointX, pointY)).addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(clickPosition));
    }

}
