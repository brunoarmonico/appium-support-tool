package core.device_handler;

import core.properties.PropertiesException;
import core.properties.PropertiesHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.remote.options.BaseOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

/**
 * Manage Appium Driver session start-up
 */
public class DriverComponent {

    private AppiumDriver driver;
    private PropertiesHelper properties = new PropertiesHelper();

    /**
     * Start Appium Driver session
     *
     * @param udid     Phone UDID serial code
     * @param platform Handled plaftorm {@link MobilePlatform}
     * @return Appium Driver instance
     */
    public AppiumDriver startDriver(String udid, String platform) {
        if (platform.equalsIgnoreCase(MobilePlatform.ANDROID)) {
            UiAutomator2Options options = UiAutomatorPropertiesCaps();
            options.setUdid(udid);
            return startDriver(options);
        } else if (platform.equalsIgnoreCase(MobilePlatform.IOS)) {
            XCUITestOptions options = XCUITestPropertiesCaps();
            options.setUdid(udid);
            return startDriver(options);
        }
        throw new RuntimeException("Device platform not listed");
    }

    /**
     * Start Appium Driver session
     *
     * @param options Appium Options {@link UiAutomator2Options} or {@link XCUITestOptions}
     * @return Appium Driver instance
     */
    public AppiumDriver startDriver(BaseOptions options) {
        String service = System.getProperty("driver.service-execution");
        Map<String, Object> farmProperties = properties.getFarmCaps(service);
        if (options instanceof UiAutomator2Options) {
            if (!farmProperties.isEmpty())
                for (Map.Entry<String, Object> entry : farmProperties.entrySet()) {
                    options.amend(entry.getKey(), entry.getValue());
                }
            if (service == null)
                driver = new AndroidDriver(getLocalService(), options);
            else
                driver = new AndroidDriver(getRemoteServiceUrl(), options);
        } else if (options instanceof XCUITestOptions) {
            if (!farmProperties.isEmpty())
                for (Map.Entry<String, Object> entry : farmProperties.entrySet()) {
                    options.amend(entry.getKey(), entry.getValue());
                }
            if (service == null)
                driver = new IOSDriver(getLocalService(), options);
            else
                driver = new IOSDriver(getRemoteServiceUrl(), options);
        } else {
            throw new RuntimeException("Only UiAutomator2Options or XCUITestOptions BaseOptions are accepted");
        }
        int implicityTime = (int) System.getProperties().get("driver.default-implicitly-timeout");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicityTime));
        return driver;
    }

    /**
     * Start local Appium Server
     *
     * @return Local Appium Driver instance
     */
    private AppiumDriverLocalService getLocalService() {
        return new AppiumServiceBuilder()
                .usingAnyFreePort()
                .withIPAddress("127.0.0.1")
                .withArgument(GeneralServerFlag.LOG_LEVEL, "info")
                .withArgument(GeneralServerFlag.BASEPATH, "/wd/")
                .usingAnyFreePort()
                .build();
    }

    /**
     * Get data for remote service
     *
     * @return URL do serviço
     */
    private URL getRemoteServiceUrl() {
        String url = System.getProperty("services.base");
        String path = System.getProperty("services.path");
        try {
            return new URL(url + path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create Options for Android using resources/properties.yml parameters
     *
     * @return UiAutomator2Options instance
     */
    private UiAutomator2Options UiAutomatorPropertiesCaps() {
        UiAutomator2Options options = new UiAutomator2Options();
        Map<String, Object> androidOptions = properties.getAndroidCaps();
        Map<String, Object> globalOptions = properties.getGlobalCaps();
        Map<String, Object> appOptions = properties.getAppCaps(MobilePlatform.ANDROID.toLowerCase());

        options.setPlatformName(MobilePlatform.ANDROID);
        if (androidOptions != null)
            for (Map.Entry<String, Object> entry : androidOptions.entrySet()) {
                options.amend(entry.getKey(), entry.getValue());
            }
        if (globalOptions != null)
            for (Map.Entry<String, Object> entry : globalOptions.entrySet()) {
                options.amend(entry.getKey(), entry.getValue());
            }
        if (appOptions != null) {
            if (appOptions.get("appPackage") == null) {
                throw new PropertiesException("Required value app.ios.appPackage not found at properties.yml");
            }
            if (appOptions.get("appActivity") == null) {
                throw new PropertiesException("Required value app.ios.appActivity not found at properties.yml");
            }
            for (Map.Entry<String, Object> entry : appOptions.entrySet()) {
                options.amend(entry.getKey(), entry.getValue());
            }
        }
        return options;
    }

    /**
     * Create Options for iOS using resources/properties.yml parameters
     *
     * @return XCUITestOptions instance
     */
    private XCUITestOptions XCUITestPropertiesCaps() {
        XCUITestOptions options = new XCUITestOptions();
        Map<String, Object> iosOptions = properties.getIosCaps();
        Map<String, Object> globalOptions = properties.getGlobalCaps();
        Map<String, Object> appOptions = properties.getAppCaps(MobilePlatform.IOS.toLowerCase());

        options.setPlatformName(MobilePlatform.IOS);
        if (iosOptions != null)
            for (Map.Entry<String, Object> entry : iosOptions.entrySet()) {
                options.amend(entry.getKey(), entry.getValue());
            }
        if (globalOptions != null)
            for (Map.Entry<String, Object> entry : globalOptions.entrySet()) {
                options.amend(entry.getKey(), entry.getValue());
            }
        if (appOptions != null) {
            if (appOptions.get("bundleId") == null) {
                throw new PropertiesException("Required value app.ios.bundleId not found at properties.yml");
            }
            for (Map.Entry<String, Object> entry : appOptions.entrySet()) {
                options.amend(entry.getKey(), entry.getValue());
            }
        }
        return options;
    }

}
