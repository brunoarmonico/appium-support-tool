package core.thread;

import io.cucumber.java.Scenario;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LocalCucumber {

    private static ThreadLocal<Scenario> cucumberScenario = new ThreadLocal<Scenario>();
    private static ThreadLocal<ExtensionContext> extensionContent = new ThreadLocal<ExtensionContext>();

    public static synchronized Scenario getScenario() {
        return cucumberScenario.get();
    }

    public static synchronized void setScenario(Scenario scenario) {
        cucumberScenario.set(scenario);
    }

    public static synchronized ExtensionContext getExtensionContent() {
        return extensionContent.get();
    }

    public static synchronized void setExtensionContent(ExtensionContext scenario) {
        extensionContent.set(scenario);
    }
}
