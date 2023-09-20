package core.device_handler;

import core.properties.PropertiesException;
import core.properties.PropertiesHelper;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Thread-safe class for driver handling
 */
public class DeviceController {
    private static LinkedList<String> deviceList;
    private static String devicePlatform;
    private static ThreadLocal<String> threadUdid = new ThreadLocal<>();

    private DeviceController() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get device UDID
     *
     * @return Local thread UDID
     */
    public static synchronized String getThreadUdid() {
        setDeviceList();
        if (threadUdid.get() == null) { // Caso não tenha um UDID definido para a thread
            String threadDevice = deviceList.get(0);
            threadUdid.set(threadDevice);
            deviceList.remove(0);
            return threadDevice;
        }
        return threadUdid.get();
    }

    /**
     * @return PlatformName running
     */
    public static synchronized String getDevicePlatform() {
        return devicePlatform;
    }

    /**
     * List devices avaiable for execution when recevied by maven or properties.yml
     */
    public static synchronized void setDeviceList() {
        if (deviceList == null) { // Caso não tenha sido listado os devices disponiveis para a execução
            new PropertiesHelper();
            deviceList = new LinkedList<String>();
            String udid = System.getProperty("driver.devices.udid");
            deviceList.addAll(Arrays.asList(udid.split(",")));
            devicePlatform = System.getProperty("driver.devices.platformName");
            String jUnitParallel = System.getProperty("cucumber.execution.parallel.config.fixed.parallelism");
            if (jUnitParallel != null) {
                if (Integer.parseInt(jUnitParallel) > deviceList.size())
                    throw new PropertiesException("Threads " + Integer.parseInt(jUnitParallel) + " devices " + deviceList.size() + " \n Threads and Devices must be the same.");
            }
        }
    }
}
