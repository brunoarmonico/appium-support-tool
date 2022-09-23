package elements.element_control;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrategyRetrive {

    AppiumDriver<MobileElement> driver;

    /**
     * Construtor da classe
     *
     * @param appiumDriver Driver de execução
     */
    public StrategyRetrive(AppiumDriver<MobileElement> appiumDriver) {
        this.driver = appiumDriver;
    }

    public StrategyRetrive() {

    }

    /**
     * Realiza a extração da estrategia de um elemento
     *
     * @param elemento elements.invisible_element.Elemento APP
     * @return Estragia de localização de elemento
     */
    public String retriveStrategy(Object elemento) {
        if (elemento instanceof By) {
            Pattern pat1 = Pattern.compile("(?<=: )(.*)");
            Matcher mat = pat1.matcher(elemento.toString());
            mat.find();
            return mat.group();
        } else {
            Pattern pat1 = Pattern.compile("((?<= -> -)(.*)([\\]]))|((?<=: )(.*))");
            Matcher mat = pat1.matcher(elemento.toString());
            mat.find();
            String stringElemento = mat.group();
            Pattern pat2 = Pattern.compile("(?<=: )(.*)(?=])|(.*)(?=}\\\\)|(.*)(?=}\\))");
            Matcher mat2 = pat2.matcher(stringElemento);
            mat2.find();
            return mat2.group();
        }
    }

    /**
     * Realiza a extração da estrategia de um elemento
     *
     * @param elemento elements.invisible_element.Elemento APP
     * @return Estragia de localização de elemento formato By
     */
    public By retriveBy(Object elemento) {
        String strategy = retriveStrategy(elemento);
        if (strategy.toLowerCase().contains("uiselector")) {
            return MobileBy.AndroidUIAutomator(strategy);
        } else if (strategy.contains("**/")) { // Class Chain
            return MobileBy.iOSClassChain(strategy);
        } else { // Predicate String
            return MobileBy.iOSNsPredicateString(strategy);
        }
    }

    /**
     * Search for N elements and return the first one found
     *
     * @param elements   Send N elements to be looked for
     * @param searchTime Set how many seconds will be used for element search
     * @return Get the first element finded
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
        return null;
    }
}
