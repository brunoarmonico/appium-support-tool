package core.appium_support.scroll;

import core.appium_support.element_control.CustomImplicit;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

/**
 * Do a Scroll!!
 *
 * @author bruno.armonico
 */
public class Scroll {

    private boolean disableException = false;
    private int defaultSpeed = 1000;
    private int defaultAreaPercentage = 70;
    AppiumDriver driver;

    /**
     * Construtor da classe
     *
     * @param appiumDriver Driver de execução
     */
    public Scroll(AppiumDriver appiumDriver) {
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
    public WebElement scrollVertical(By targetElement, double screenPercentage, int scrollSpeed, int scrollAmount, Toward scrollTo) {
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

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 0);
        int centroX = (proporcaoX / 2);
        if (scrollTo == Toward.DOWN) { // Scroll Down
            int direcaoAreaMin = (int) (((double) proporcaoY / 2) * ((screenPercentage / 100) + 1));
            int direcaoAreaMax = (int) (((double) proporcaoY / 2) * (100 - screenPercentage) / 100);

            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centroX,  direcaoAreaMin));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(scrollSpeed), PointerInput.Origin.viewport(), centroX, direcaoAreaMax));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(scroll));
        } else { // Scroll Up
            int direcaoAreaMin = (int) (((double) proporcaoY / 2) * (100 - screenPercentage) / 100);
            int direcaoAreaMax = (int) (((double) proporcaoY / 2) * ((screenPercentage / 100) + 1));

            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centroX,  direcaoAreaMin));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(scrollSpeed), PointerInput.Origin.viewport(), centroX, direcaoAreaMax));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(scroll));
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
    public WebElement scrollVertical(By targetElement, int scrollAmount, Toward scrollTo) {
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
    public WebElement scrollArea(WebElement elementArea, By targetElement, double areaPercentage, int scrollSpeed, int scrollAmount, Toward scrollTo) {
        if (targetElement != null) {
            if (new CustomImplicit(driver).setImplicity(1).elementExists(targetElement) && driver.findElement(targetElement).isDisplayed()) {
                return driver.findElement(targetElement);
            }
        }
        int areaLocationX = elementArea.getLocation().getX() != 0 ?
                elementArea.getLocation().getX() : (int) (elementArea.getSize().getWidth() * 0.1);
        int areaLocationY = elementArea.getLocation().getY() != 0 ?
                elementArea.getLocation().getY() : (int) (elementArea.getLocation().getY() * 0.1);
        int areaSizeX = elementArea.getSize().getWidth();
        int areaSizeY = elementArea.getSize().getHeight();

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 0);
        if (scrollTo == Toward.DOWN) {
            int areaCenter = areaSizeY / 2;
            int direcaoAreaMin = (int) (areaLocationY + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationY + ((areaCenter) * (areaPercentage / 100)));
            int centroX = (areaLocationX + (areaSizeX / 2));

            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centroX, direcaoAreaMax));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(scrollSpeed), PointerInput.Origin.viewport(), centroX, direcaoAreaMin));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(scroll));
        } else if (scrollTo == Toward.UP) {
            int areaCenter = areaSizeY / 2;
            int direcaoAreaMin = (int) (areaLocationY + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationY + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroX = (areaLocationX + (areaSizeX / 2));

            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centroX,  direcaoAreaMin));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(scrollSpeed), PointerInput.Origin.viewport(), centroX, direcaoAreaMax));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(scroll));
        } else if (scrollTo == Toward.LEFT) {
            int areaCenter = areaLocationX / 2;
            int direcaoAreaMin = (int) (areaLocationX + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationX + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroY = areaLocationY + (areaSizeY / 2);

            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), direcaoAreaMin,  centroY));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(scrollSpeed), PointerInput.Origin.viewport(), direcaoAreaMax, centroY));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(scroll));
        } else if (scrollTo == Toward.RIGHT) {
            int areaCenter = areaLocationX / 2;
            int direcaoAreaMin = (int) (areaLocationX + ((areaCenter) * ((100 - areaPercentage) / 100)));
            int direcaoAreaMax = (int) (areaLocationX + ((areaCenter) * ((areaPercentage / 100) + 1)));
            int centroY = (areaLocationY + (areaSizeY / 2));

            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), direcaoAreaMax,  centroY));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(scrollSpeed), PointerInput.Origin.viewport(), direcaoAreaMin, centroY));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Arrays.asList(scroll));
        }
        scrollAmount--;
        if (targetElement != null) {
             if (new CustomImplicit(driver).setImplicity(1).elementExists(targetElement) && driver.findElement(targetElement).isDisplayed()) {
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
    public WebElement scrollArea(WebElement elementArea, By targetElement, int scrollAmount, Toward scrollTo) {
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
    public void scrollArea(WebElement elementArea, int scrollAmount, Toward scrollTo) {
        scrollArea(elementArea, null, defaultAreaPercentage, defaultSpeed, scrollAmount, scrollTo);
    }
}
