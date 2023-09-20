package po;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Appium8 {

    @FindBy(css = "Web")
    @AndroidFindBy(uiAutomator = "Android")
    @iOSXCUITFindBy(iOSClassChain = "iOS")
    WebElement elemento;

}
