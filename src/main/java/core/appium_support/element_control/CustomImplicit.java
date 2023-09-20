package core.appium_support.element_control;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Define uma tempo diferenciado de implicit para busca por elementos
 * @author bruno.armonico
 */
public class CustomImplicit {

    private int DEFAULT_IMPLICITY = (int) System.getProperties().get("driver.default-implicitly-timeout");
    private int SEARCH_IMPLICITY = 1;
    private AppiumDriver driver;


    public CustomImplicit (AppiumDriver driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_IMPLICITY));
    }

    public CustomImplicit setImplicity(int segundos) {
        SEARCH_IMPLICITY = segundos;
        return this;
    }

    public boolean elementExists(List<WebElement> elemento) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_IMPLICITY));
        // Se lista de elemento não esta vazio
        return !elemento.isEmpty();
    }

    /**
     * Verifica se um elemento existe utilizando menor valor de implicity e elemento tipo By
     *
     * @param elemento elements.invisible_element.Elemento tipo By
     * @return Validação boleana se o elemento existe ou não
     */
    public boolean elementExists(By elemento) {
        driver.manage().timeouts().implicitlyWait(SEARCH_IMPLICITY, TimeUnit.SECONDS);
        String elemString = new StrategyRetrive().retriveStrategy(elemento);
        List<WebElement> lstElemento;
        if (elemString.toLowerCase().contains("uiselector")) {
            lstElemento = driver.findElements(AppiumBy.androidUIAutomator(elemString));
        } else if (elemString.contains("**/")) { // Class Chain
            lstElemento = driver.findElements(AppiumBy.iOSClassChain(elemString));
        } else { // Predicate String
            lstElemento = driver.findElements(AppiumBy.iOSNsPredicateString(elemString));
        }
        // Se elemento encontrado
        boolean elementoExiste = !lstElemento.isEmpty();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_IMPLICITY));
        return elementoExiste;
    }
}
