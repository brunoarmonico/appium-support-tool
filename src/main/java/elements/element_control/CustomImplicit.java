package elements.element_control;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Define uma tempo diferenciado de implicit para busca por elementos
 * @author bruno.armonico
 */
public class CustomImplicit {

    private static int DEFAULT_IMPLICITY = 30;
    private int SEARCH_IMPLICITY = 1;
    private AppiumDriver<MobileElement> driver;


    public CustomImplicit (AppiumDriver<MobileElement> driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICITY, TimeUnit.SECONDS);
    }

    public CustomImplicit setImplicity(int segundos) {
        SEARCH_IMPLICITY = segundos;
        return this;
    }

    public boolean elementExists(List<MobileElement> elemento) {
        driver.manage().timeouts().implicitlyWait(SEARCH_IMPLICITY, TimeUnit.SECONDS);
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
        List<MobileElement> lstElemento;
        if (elemString.toLowerCase().contains("uiselector")) {
            lstElemento = driver.findElements(MobileBy.AndroidUIAutomator(elemString));
        } else if (elemString.contains("**/")) { // Class Chain
            lstElemento = driver.findElements(MobileBy.iOSClassChain(elemString));
        } else { // Predicate String
            lstElemento = driver.findElements(MobileBy.iOSNsPredicateString(elemString));
        }
        // Se elemento encontrado
        boolean elementoExiste = !lstElemento.isEmpty();
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICITY, TimeUnit.SECONDS);
        return elementoExiste;
    }
}
