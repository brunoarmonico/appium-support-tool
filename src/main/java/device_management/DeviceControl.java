package device_management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.*;

public class DeviceControl {

    private static final Logger logger = (Logger) LogManager.getLogger(DeviceControl.class);
    private static Map<Long, Map<String, Object>> deviceControl = new LinkedHashMap<>();

    public static synchronized void newDeviceList(String deviceList) {
        if (deviceControl.isEmpty()) {
            try {
                deviceControl = new LinkedHashMap<>();
                JSONArray deviceJson = new JSONArray(deviceList);
                List<Long> threadIds = new ArrayList<>();
                for (Thread thread : Thread.getAllStackTraces().keySet()) { // Suite execution
                    if (thread.getName().contains("TestNG-tests")) {
                        threadIds.add(thread.getId());
                    }
                }
                if (threadIds.isEmpty()) //Direct execution
                    threadIds.add(Thread.currentThread().getId());
                int deviceCount = deviceJson.toList().size();
                if (threadIds.size() > deviceCount) {
                    throw new DeviceManagementException("TestNG threads doesn't match JSON device value");
                }
                while (deviceCount-- > 0) {
                    Map<String, Object> deviceParameters = deviceJson.getJSONObject(deviceCount).toMap();
                    deviceControl.put(threadIds.get(deviceCount), deviceParameters);
                    logger.info("New device received");
                }
            } catch (JSONException e) {
                throw new DeviceManagementException("Something happens while trying to process device JSON");
            }
        } else {
            logger.info("Device control list already filled");
        }
    }

    public static synchronized Map<String, Object> getDevice() {
        long threadId = Thread.currentThread().getId();
        return deviceControl.getOrDefault(threadId, null);
    }

    public static synchronized void startDeviceBooking() {
        getDevice().put("isRunning", true);
        getDevice().put("checkIn", LocalDateTime.now());
        logger.info("Created device booking");
    }

    public static synchronized void endDeviceBooking() {
        getDevice().put("isRunning", false);
        logger.info("Closed device booking");
    }

    /**
     * Check running device status
     *
     * @return boolean validation for actual device status
     */
    public static synchronized boolean deviceStatus() {
        return (boolean) getDevice().get("isRunning");
    }
}
