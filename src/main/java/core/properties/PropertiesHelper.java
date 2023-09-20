package core.properties;

import io.appium.java_client.remote.MobilePlatform;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Recebe dados do arquivo de propriedades yaml
 *
 * Esta classe repassa as properties do arquivo yaml para o System.Properties
 * Tambem é usada como acesso facil à algumas propriedades
 */
public class PropertiesHelper {

    Map<String, Object> properties = new PropertiesReader().getYamlProperties();

    public PropertiesHelper() {
        updateProperties();
        setDefaultDriverProperties();
        setDefaultServiceProperties();
        setFrameworkProperties();
    }

    /**
     * Verifica a necessidade de atualização do arquivo de properties
     */
    private void updateProperties() {
        DefaultProperties defaultProps = new DefaultProperties();
        Map<String, Object> defaultMap = defaultProps.defaultPropertiesMap();
        if (defaultProps.compareProperties(defaultMap, properties)) {
            defaultProps.overwriteYamlProperties(properties);
            new PropertiesReader().refreshProperties();
        }
    }

    /**
     * Aplica nas propriedades do sistema os parametros de properties e maven command
     */
    private void setDefaultDriverProperties() {
        Properties props = System.getProperties();

        // driver implicity
        Map<String, Object> driverCaps = getDriverCaps();
        props.put("driver.default-implicitly-timeout", driverCaps.get("default-implicitly-timeout"));
        if (props.get("driver.default-implicitly-timeout") == null) {
            throw new PropertiesException("Set Implicity Wait default value in 'driver > default-implicitly-timeout' at properties.yml");
        } // driver service
        if (System.getProperty("service") != null) {
            props.put("driver.service-execution", props.get("service"));
        } else if (driverCaps.get("service-execution") != null) {
            props.put("driver.service-execution", driverCaps.get("service-execution"));
        }
        // driver devices
        props.put("driver.devices.udid", ((Map<String, String>) driverCaps.get("devices")).get("udid"));
        props.put("driver.devices.platformName", ((Map<String, String>) driverCaps.get("devices")).get("platformName"));
        if (System.getProperty("androidDevice") != null) {
            props.put("driver.devices.udid", props.get("androidDevice"));
            props.put("driver.devices.platformName", MobilePlatform.ANDROID);
        } else if (System.getProperty("iosDevice") != null) {
            props.put("driver.devices.udid", props.get("iosDevice"));
            props.put("driver.devices.platformName", MobilePlatform.IOS);
        }
        if (System.getProperty("androidDevice") != null && System.getProperty("iosDevice") != null) {
            throw new RuntimeException("Android and iOS simultaneously executions is not Allowed!!");
        }
        if (props.get("driver.devices.udid") == null) {
            throw new PropertiesException("Set at least 1 UDID by -DandroidDevice, -DiosDevice maven flags or properies.yml");
        }
    }

    /**
     * Aplica nas propriedades do sistema os parametros de properties e maven command
     */
    private void setDefaultServiceProperties() {
        Properties props = System.getProperties();
        String service = (String) props.get("driver.service-execution");
        if (service != null) {
            Map<String, Object> serviceCaps = getServiceCaps(service);
            props.put("services.base", serviceCaps.get("base"));
            props.put("services.path", serviceCaps.get("path"));
        }
    }

    /**
     * Aplica nas propriedades do sistema os parametros de properties e maven command
     */
    private void setFrameworkProperties() {
        Properties props = System.getProperties();
        Map<String, Object> frameCaps = (Map<String, Object>) properties.get("framework");

        Map<String, String> screenshotEvidence = (Map<String, String>) frameCaps.get("screenshot-evidence");
        if (screenshotEvidence.get("assertion") == null)
            props.put("framework.screenshot-evidence.assertion", "all");
        else
            props.put("framework.screenshot-evidence.assertion", screenshotEvidence.get("assertion"));

        Map<String, String> videoEvidence = (Map<String, String>) frameCaps.get("video-evidence");
        if (videoEvidence.get("enabled") == null)
            props.put("framework.video-evidence.enabled", "false");
        else
            props.put("framework.video-evidence.enabled", videoEvidence.get("enabled"));
        if (videoEvidence.get("assertion") == null)
            props.put("framework.video-evidence.assertion", "all");
        else
            props.put("framework.video-evidence.assertion", videoEvidence.get("assertion"));

        if (videoEvidence.get("localFile") == null)
            props.put("framework.video-evidence.localFile", "false");
        else
            props.put("framework.video-evidence.localFile", videoEvidence.get("localFile"));
    }

    public Map<String, Object> getGlobalCaps() {
        Map<String, Object> devices = (Map<String, Object>) properties.get("capabilities");
        return (Map<String, Object>) devices.get("global");
    }

    public Map<String, Object> getAndroidCaps() {
        Map<String, Object> devices = (Map<String, Object>) properties.get("capabilities");
        devices = ((Map<String, Object>) devices.get("android"));
        return devices;
    }

    public Map<String, Object> getIosCaps() {
        Map<String, Object> devices = (Map<String, Object>) properties.get("capabilities");
        return (Map<String, Object>) devices.get("ios");
    }

    public Map<String, Object> getDriverCaps() {
        return (Map<String, Object>) properties.get("driver");
    }

    public Map<String, Object> getServiceCaps(String serviceName) {
        if (serviceName == null)
            return Collections.emptyMap();
        Map<String, Object> servicesProperties = (Map<String, Object>) properties.get("services");
        servicesProperties = (Map<String, Object>) servicesProperties.get(serviceName);
        if (servicesProperties == null) {
            throw new PropertiesException("Service not found in 'services > " + serviceName + "' at properties.yml");
        }
        return servicesProperties;
    }

    public Map<String, Object> getFarmCaps(String service) {
        Map<String, Object> serviceCaps = getServiceCaps(service);
        if (serviceCaps.isEmpty()) {
            return Collections.emptyMap();
        }
        if (serviceCaps.get("capabilities") == null) {
            return Collections.emptyMap();
        }
        return (Map<String, Object>) serviceCaps.get("capabilities");
    }

    public Map<String, Object> getAppCaps(String platform) {
        Map<String, Object> app = (Map<String, Object>) properties.get("app");
        app = (Map<String, Object>) app.get(platform);
        if (app == null) {
            throw new PropertiesException("Plataform not found in 'app > " + platform + "' at properties.yml");
        }
        return app;
    }
}
