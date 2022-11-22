# Appium support tool

This is a bunch of tools that I created to help to deal with appium.

### Device Management
Here we have some code to help with parallel execution with TestNG.

To manage your devices you need send in JSON array format, every new braces will count like 1 device
````
[
    {"udid" : "value",
    "operational_system", "value",
    "system_version", "10"
    }, 
    {"udid" : "value",
    "operational_system", "value",
    "system_version", "8"
    },
    ... N
]
````

Before start to run your tests receive all device parameters, you cannot increase or decrease your device amount while tests are running. 
````
@BeforeClass
@Parameters({"parallelDevices"})
public void beforeRunTest(@Optional("") String parallelDevices) {
    if (!parallelDevices.isEmpty()) {
    DeviceControl.newDeviceList(parallelDevices);
    DeviceControl.getDevice().putIfAbsent("params", new TestInfo());
    }
}
````

How to get your parameters
````
public void myTest() {
    //Get recived parameters
    if (DeviceControl.getDevice() != null) {
        udid = ((String) DeviceControl.getDevice().get("udid"));
        so = ((String) DeviceControl.getDevice().get("operational_system"));
        something = ((OtherClass) DeviceControl.getDevice().get("things"));
        value = ((int) DeviceControl.getDevice().get("value"));
    }
    //YOUR TEST CODE
}
````

From your TestNG Suite you need set correlate the device number from your JSON and the thread-count value in the TestNG Suite.

If you are running and sending this parameters by Jenkins and Groovy, you can run in this way: 
````
def deviceValue = PARAMETERS.split("(?<=}),").length as String
mvn test -Dsurefire.suiteXmlFiles=src/test.xml -DthreadCount=deviceValue  -DparallelDevices=PARAMETERS
````

### Scrolling

You can do a scroll calling the Scroll class and send it custom parameters to adjust the scroll as you need.
Works with Android and iOS.


### Invisible Elements (Android)

There some webviews pages in Android that could be incomplete due to UiAutomator problems, that package help to get this objects with the allowInvisibleElements flag.
This class helps you to:
* Find and click in elements
* Automatically scroll to a element
* Find an element by their children
* Wait for some conditions


Unfortunately, I don't have much time to translate all this code and java doc to english, so for some time will be in portuguese.

Feel free to use and modify, and i will be happy if i could receive some credits.
