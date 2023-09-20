package core.thread;

import core.appium_support.device_log.AdbLogcat;
import core.device_handler.DeviceController;
import core.device_handler.DriverComponent;
import core.properties.PropertiesHelper;
import core.test_support.ScreenRecording;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.options.BaseOptions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe thread-safe para controle do paralelismo
 */
public class LocalDevice {

    private static ThreadLocal<AppiumDriver> appiumDriver = new ThreadLocal<AppiumDriver>();
    private static ThreadLocal<Map<String, String>> executionArgs = new ThreadLocal<Map<String, String>>();
    private static ThreadLocal<AdbLogcat> logcat = new ThreadLocal<AdbLogcat>();

    private LocalDevice() {
        throw new IllegalStateException("Utility class");
    }



    /**
     * Inicializa um novo Appium Driver para a thread atual
     *
     * @param deviceArgs Argumentos de identificação do device, UDID e platformName
     */
    public static synchronized void startDriver(Map<String, String> deviceArgs) {
        appiumDriver.set(new DriverComponent().startDriver(deviceArgs.get("udid"), deviceArgs.get("platformName")));
        new ScreenRecording().startRecording();
    }

    /**
     * Inicializa um novo Appium Driver para a thread atual com Options
     *
     * @param options Options do Appium {@link UiAutomator2Options} ou {@link XCUITestOptions}
     */
    public static synchronized void startDriver(BaseOptions options) {
        appiumDriver.set(new DriverComponent().startDriver(options));
    }

    /**
     * Recebe o Appium Driver da thread atual
     *
     * @return Appium Driver
     */
    public static synchronized AppiumDriver getDriver() {
        return appiumDriver.get();
    }

    /**
     * Recebe os argumentos de execução, via maven -D args ou caso não exista retira do arquivo de properties
     * Via Maven será aceito argumentos do tipo androidDevice ou iosDevice e service
     *
     * @return Map com dados de execução
     */
    public static synchronized Map<String, String> getDeviceExecutionArgs() {
        if (executionArgs.get() != null)
            return executionArgs.get();
        new PropertiesHelper();

        LinkedHashMap<String, String> args = new LinkedHashMap<>();
        args.put("udid", DeviceController.getThreadUdid());
        args.put("platformName", DeviceController.getDevicePlatform());
        executionArgs.set(args);
        return executionArgs.get();
    }

    public static AdbLogcat getLogcat() {
        return logcat.get();
    }

    public static void setLogcat(AdbLogcat logcat) {
        LocalDevice.logcat.set(logcat);
    }
}
