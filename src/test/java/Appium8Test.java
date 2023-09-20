import core.thread.LocalDevice;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;

public class Appium8Test {

    public static void main(String[] args) {
        new Appium8Test().test();
    }

    @Test
    public void test() {
//        XCUITestOptions options = new XCUITestOptions();
        UiAutomator2Options options = new UiAutomator2Options();
        options.setUdid("emulator-5554");
        options.doesNoReset();
        options.setAppPackage("com.android.chrome");
        options.setAppActivity("com.google.android.apps.chrome.Main");
//        options.amend("platformName", "android");
//        BaseOptions base = new BaseOptions<>();
//        base.merge(options);

        System.out.println("CAPS: " + options);
        LocalDevice.startDriver(options);

        AppiumDriver driver = LocalDevice.getDriver();
    }

}
