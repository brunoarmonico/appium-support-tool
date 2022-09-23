package elements.scroll;

import elements.element_control.CustomImplicit;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;

import java.time.Duration;

/**
 * Do a Scroll!!
 *
 * @author bruno.armonico
 */
public class Scroll {

    private boolean disableException = false;
    private int defaultSpeed = 1000;
    private int defaultAreaPercentage = 70;
    AppiumDriver<MobileElement> driver;

    /**
     * Construtor da classe
     *
     * @param appiumDriver Driver de execução
     */
    public Scroll(AppiumDriver<MobileElement> appiumDriver) {
        this.driver = appiumDriver;
    }

    /**
     * Disable exception when element is not finded
     *
     * @param disableException Turn ON or OFF element exception
     * @return Return instance of Scroll
     */
    public Scroll disableException(boolean disableException) {
        this.disableException = disableException;
        return this;
    }

    /**
     * Run a custom scroll
     *
     * @param targetElement  Element do be searched
     * @param screenPercentage Sets the percentage of phone screen used to scroll
     * @param scrollSpeed    Sets the scroll speed
     * @param scrollAmount   Maximum scrolls tries while searching
     * @param scrollTo       Sets the scroll direction
     */
    public MobileElement scrollVertical(By targetElement, double screenPercentage, int scrollSpeed, int scrollAmount, Toward scrollTo) {
        if (scrollTo == Toward.RIGHT || scrollTo == Toward.LEFT) {
            new ScrollException("Horizontal scroll not allowed!");
        }
        if (targetElement != null) {
            if (new CustomImplicit(driver).setImplicity(1).elementExists(targetElement)) {
                return driver.findElement(targetElement);
            }
        }
        int proporcaoX = driver.manage().window().getSize().getWidth();
        int proporcaoY = driver.manage().window().getSize().getHeight();
        if (scrollTo == Toward.DOWN) { // Scroll Down
            new TouchAction(driver).press(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * ((screenPercentage / 100) + 1)))).
                    waitAction(WaitOptions.waitOptions(Duration.ofMillis(scrollSpeed))).
                    moveTo(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * (100 - screenPercentage) / 100))).
                    release().perform();
        } else { // Scroll Up
            new TouchAction(driver).press(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * (100 - screenPercentage) / 100))).
                    waitAction(WaitOptions.waitOptions(Duration.ofMillis(scrollSpeed))).
                    moveTo(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * ((screenPercentage / 100) + 1)))).
                    release().perform();
        }
        scrollAmount--;
        if (targetElement != null) {
            if (new CustomImplicit(driver).setImplicity(1).elementExists(targetElement)) {
                return driver.findElement(targetElement);
            }
        }
        if (scrollAmount > 0) {
            scrollVertical(targetElement, screenPercentage, scrollSpeed, scrollAmount, scrollTo);
        }
        if (targetElement != null) {
            if (disableException) {
                if (!new CustomImplicit(driver).setImplicity(1).elementExists(targetElement))
                    return null;
            } else {
                return driver.findElement(targetElement);
            }
        }
        return null;
    }

    /**
     * Do a scroll to find target element
     *
     * @param targetElement Element do be searched
     * @param scrollAmount  Maximum scrolls tries while searching
     * @param scrollTo      Sets the scroll direction
     *
     * @return Return target element if finded
     */
    public MobileElement scrollVertical(By targetElement, int scrollAmount, Toward scrollTo) {
        return scrollVertical(targetElement, 50, defaultSpeed, scrollAmount, scrollTo);
    }


    /**
     * Run a custom scroll in an element area
     *
     * @param elementArea    Sets the area used to scroll
     * @param targetElement  Element do be searched
     * @param areaPercentage Sets the percentage of area used to scroll
     * @param scrollSpeed    Sets the scroll speed
     * @param scrollAmount   Maximum scrolls tries while searching
     * @param scrollTo       Sets the scroll direction
     */
    public MobileElement scrollArea(MobileElement elementArea, By targetElement, double areaPercentage, int scrollSpeed, int scrollAmount, Toward scrollTo) {
        if (targetElement != null) {
            if (new CustomImplicit(driver).setImplicity(1).elementExists(targetElement)) {
                return driver.findElement(targetElement);
            }
        }
        int areaLocationX = elementArea.getLocation().getX();
        int areaLocationY = elementArea.getLocation().getY();
        int areaSizeX = elementArea.getSize().getWidth();
        int areaSizeY = elementArea.getSize().getHeight();
        if (scrollTo == Toward.DOWN) {
            int areaCenter = areaSizeY / 2;
            int direcaoAreaMin = (int) (areaLocationY + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationY + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroX = (areaLocationX + (areaSizeX / 2));
            new TouchAction(driver).press(PointOption.point(centroX, direcaoAreaMax)).
                    waitAction(WaitOptions.waitOptions(Duration.ofMillis(scrollSpeed))).
                    moveTo(PointOption.point(centroX, direcaoAreaMin)).
                    release().perform();
        } else if (scrollTo == Toward.UP) {
            int areaCenter = areaSizeY / 2;
            int direcaoAreaMin = (int) (areaLocationY + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationY + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroX = (areaSizeX + (areaLocationX / 2));
            new TouchAction(driver).press(PointOption.point(centroX, direcaoAreaMin)).
                    waitAction(WaitOptions.waitOptions(Duration.ofMillis(scrollSpeed))).
                    moveTo(PointOption.point(centroX, direcaoAreaMax)).
                    release().perform();
        } else if (scrollTo == Toward.LEFT) {
            int areaCenter = areaLocationX / 2;
            int direcaoAreaMin = (int) (areaLocationX + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationX + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroY = areaLocationY + (areaSizeY / 2);
            new TouchAction(driver).press(PointOption.point(direcaoAreaMin, centroY)).
                    waitAction(WaitOptions.waitOptions(Duration.ofMillis(scrollSpeed))).
                    moveTo(PointOption.point(direcaoAreaMax, centroY)).
                    release().perform();
        } else if (scrollTo == Toward.RIGHT) {
            int areaCenter = areaLocationX / 2;
            int direcaoAreaMin = (int) (areaLocationX + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationX + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroY = (areaLocationY + (areaSizeY / 2));
            new TouchAction(driver).press(PointOption.point(direcaoAreaMax, centroY)).
                    waitAction(WaitOptions.waitOptions(Duration.ofMillis(scrollSpeed))).
                    moveTo(PointOption.point(direcaoAreaMin, centroY)).
                    release().perform();
        }
        scrollAmount--;
        if (targetElement != null) {
            if (new CustomImplicit(driver).setImplicity(1).elementExists(targetElement)) {
                return driver.findElement(targetElement);
            }
        }
        if (scrollAmount > 0) {
            scrollArea(elementArea, targetElement, areaPercentage, scrollSpeed, scrollAmount, scrollTo);
        }
        if (targetElement != null) {
            return driver.findElement(targetElement);
        } else {
            return null;
        }
    }

    /**
     * Run a custom scroll in an element area
     * Use default areaPercentage and scrollSpeed
     *
     * @param elementArea    Sets the area used to scroll
     * @param targetElement  Element do be searched
     * @param scrollAmount   Maximum scrolls tries while searching
     * @param scrollTo       Sets the scroll direction
     */
    public MobileElement scrollArea(MobileElement elementArea, By targetElement, int scrollAmount, Toward scrollTo) {
        return scrollArea(elementArea, targetElement, defaultAreaPercentage, defaultSpeed, scrollAmount, scrollTo);
    }

    /**
     * Run a custom scroll in an element area without target element
     * Use default areaPercentage and scrollSpeed
     *
     * @param elementArea    Sets the area used to scroll
     * @param scrollAmount   Maximum scrolls tries while searching
     * @param scrollTo       Sets the scroll direction
     */
    public void scrollArea(MobileElement elementArea, int scrollAmount, Toward scrollTo) {
        scrollArea(elementArea, null, defaultAreaPercentage, defaultSpeed, scrollAmount, scrollTo);
    }
}
