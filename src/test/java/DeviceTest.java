import device_management.DeviceControl;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class DeviceTest {

    @Test
    public void deviceManagementTest() {
        DeviceControl.newDeviceList("[{udid :'deviceUdid', user : 'loggedUser', userPass : 'userPass'}, {udid :'deviceUdid2', user : 'loggedUser2', userPass : 'userPass2'}]");
        System.out.println("[test] Device: " + DeviceControl.getDevice());
        System.out.println("[test] Device udid: " + DeviceControl.getDevice().get("udid"));
    }

    @Test
    @Parameters("allDevices")
    public void suiteTest(String allDevices) {
        DeviceControl.newDeviceList(allDevices);
        DeviceControl.startDeviceBooking();
        System.out.println("[test] Device: " + DeviceControl.getDevice());
        DeviceControl.getDevice().put("newParam" , "Novo parametro");
        DeviceControl.endDeviceBooking();
        System.out.println("[test] Device: " + DeviceControl.getDevice());
    }
}
