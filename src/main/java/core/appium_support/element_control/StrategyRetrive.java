package core.appium_support.element_control;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrive strategy for By or MobileElement elements!!
 *
 * @author bruno.armonico
 */
public class StrategyRetrive {

    AppiumDriver driver;

    /**
     * @param appiumDriver Executor driver
     */
    public StrategyRetrive(AppiumDriver appiumDriver) {
        this.driver = appiumDriver;
    }

    public StrategyRetrive() {

    }

    /**
     * Extract element strategy for By or MobileElement
     * Supported: UiSelector, Class Chain, Predicate String
     *
     * @param element Element in By or MobileElement object
     * @return Element strategy as String
     */
    public String retriveStrategy(Object element) {
        if (element instanceof By) {
            Matcher match = Pattern.compile("(?<=: )(.*)").matcher(element.toString());
            match.find();
            return match.group();
        } else {
            Matcher mat = Pattern.compile("((?<= -> -)(.*)([\\]]))|((?<=: )(.*))").matcher(element.toString());
            mat.find();
            String strategyCut = mat.group();
            Matcher match = Pattern.compile("(?<=: )(.*)(?=])|(.*)(?=}\\\\)|(.*)(?=}\\))").matcher(strategyCut);
            match.find();
            return match.group();
        }
    }

    /**
     * Extract element strategt for By or MobileElement
     * Supported: UiSelector, Class Chain, Predicate String
     *
     * @param element Element in By or MobileElement object
     * @return Return element as By object
     */
    public By retriveBy(Object element) {
        String strategy = retriveStrategy(element);
        if (strategy.toLowerCase().contains("uiselector")) {
            return AppiumBy.androidUIAutomator(strategy);
        } else if (strategy.contains("**/")) { // Class Chain
            return AppiumBy.iOSClassChain(strategy);
        } else { // Predicate String
            return AppiumBy.iOSNsPredicateString(strategy);
        }
    }

    /**
     * Search for N elements and return the first one found
     *
     * @param elements   Send N elements to be looked for
     * @param searchTime Set how many seconds will be used for element search
     * @return Get the first element find
     */
    public By findFirst(int searchTime, By... elements) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= searchTime) {
            for (By element : elements) {
                if (new CustomImplicit(driver).setImplicity(1).elementExists(element))
                    return element;
            }
        }
        System.out.println("Element not found at findFirst");
        return null;
    }
}
