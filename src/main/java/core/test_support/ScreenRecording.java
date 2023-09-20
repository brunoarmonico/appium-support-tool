package core.test_support;

import core.thread.LocalCucumber;
import core.thread.LocalDevice;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.android.AndroidStopScreenRecordingOptions;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSStopScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.screenrecording.ScreenRecordingUploadOptions;
import io.cucumber.java.Scenario;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Appium Screen recording
 */
public class ScreenRecording {

    private final String video_path = "target/video-evidence/";

    private static final ThreadLocal<Boolean> recording = new ThreadLocal<Boolean>();

    public void startRecording() {
        if (!(Boolean) System.getProperties().get("framework.video-evidence.enabled"))
            return;
        try {
            AppiumDriver driver = LocalDevice.getDriver();
            if (driver.getCapabilities().getPlatformName().is(Platform.ANDROID)) {
                ((CanRecordScreen) driver).startRecordingScreen(new AndroidStartScreenRecordingOptions().enableBugReport());
            } else {
                ((CanRecordScreen) driver).startRecordingScreen(new IOSStartScreenRecordingOptions());
            }
        } catch (WebDriverException e) {
            recording.set(false);
        }
        recording.set(true);
    }

    public void stopRecording() {
        if (!(Boolean) System.getProperties().get("framework.video-evidence.enabled") || ! recording.get())
            return;
        AppiumDriver driver = LocalDevice.getDriver();
        Scenario scenario = LocalCucumber.getScenario();

        String assertOption = (String) System.getProperties().get("framework.video-evidence.assertion");
        if ((!scenario.isFailed() && (assertOption.equalsIgnoreCase("all") || assertOption.equalsIgnoreCase("fail")))
                ||
                (scenario.isFailed() && (assertOption.equalsIgnoreCase("all") || assertOption.equalsIgnoreCase("pass")))) {
            ((CanRecordScreen) driver).stopRecordingScreen();
            return;
        }
        String base64Video = "";
        if (driver.getCapabilities().getPlatformName().is(Platform.ANDROID))
            base64Video = ((CanRecordScreen) driver).stopRecordingScreen(new AndroidStopScreenRecordingOptions().withUploadOptions(ScreenRecordingUploadOptions.uploadOptions()));
        else
            base64Video = ((CanRecordScreen) driver).stopRecordingScreen(new IOSStopScreenRecordingOptions().withUploadOptions(ScreenRecordingUploadOptions.uploadOptions()));

        byte[] videoByte = Base64.decodeBase64(base64Video);

        scenario.attach(videoByte, "video/mp4", "Video");

        if ((Boolean) System.getProperties().get("framework.video-evidence.localFile")) {
            String scenarioName = scenario.getName();
            int scenarioLine = scenario.getLine();
            String scenarioStatus = scenario.getStatus().name();

            String destinationPath = video_path + scenarioName.replaceAll("[<>:\"/\\\\|?*]", " ") + "_" + scenarioLine + "_" + scenarioStatus + ".mp4";
            Path path = Paths.get(destinationPath);
            try {
                Files.createDirectories(Paths.get(video_path));
                Files.write(path, videoByte);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
