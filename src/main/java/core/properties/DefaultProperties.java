package core.properties;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create YAML default properties
 */
public class DefaultProperties {

    private final String yamlPath = "src/main/resources/";
    private boolean updateFile = false;
    private final String yamlFileName = "properties.yml";
    private final String appPackage = "androidPackageHere";
    private final String appActivity = "androidActivityHere";
    private final String bundleId = "iosBundleIdHere";
    private final String localBaseURL = "http://127.0.0.1:4723";
    private final String localPath = "/wd/hub";

    /**
     * Recebe e cria um arquivo de properties padrão
     */
    protected void createDefaultYamlProperties() {
        Map<String, Object> properties = defaultPropertiesMap();
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        Yaml yaml = new Yaml(options);
        try {
            Files.createDirectories(Paths.get(yamlPath));
            FileWriter writer = new FileWriter(yamlPath + yamlFileName);
            yaml.dump(properties, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza o arquivo de properties
     */
    protected void overwriteYamlProperties(Map<String, Object> properties) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        Yaml yaml = new Yaml(options);
        try {
            Files.createDirectories(Paths.get(yamlPath));
            FileWriter writer = new FileWriter(yamlPath + yamlFileName);
            yaml.dump(properties, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cria um map com as propriedades padrão do arquivo YAML
     *
     * @return Map com properties
     */
    protected Map<String, Object> defaultPropertiesMap() {
        return new LinkedHashMap<String, Object>() {{
            put("app", new LinkedHashMap<String, Object>() {{
                put("android", new LinkedHashMap<String, Object>() {{
                    put("appPackage", appPackage);
                    put("appActivity", appActivity);
                }});
                put("ios", new LinkedHashMap<String, Object>() {{
                    put("bundleId", bundleId);
                }});
            }});
            put("services", new LinkedHashMap<String, Object>() {{
                put("device-farm", new LinkedHashMap<String, Object>() {{
                    put("base", "");
                    put("path", "");
                    put("capabilities", new LinkedHashMap<String, Object>() {{
                        put("farmCaps", ". . .");
                    }});
                }});
                put("local", new LinkedHashMap<String, Object>() {{
                    put("base", localBaseURL);
                    put("path", localPath);
                }});
            }});
            put("capabilities", new LinkedHashMap<String, Object>() {{
                put("android", new LinkedHashMap<String, Object>() {{
                    put("autoGrantPermissions", true);
                }});
                put("ios", new LinkedHashMap<String, Object>() {{
                    put("platformVersion", 17);
                }});
                put("global", new LinkedHashMap<String, Object>() {{
                    put("newCommandTimeout", 60);
                    put("autoAcceptAlerts", true);
                    put("appium:noReset", true);
                    put("appium:fullReset", false);
                }});
            }});
            put("driver", new LinkedHashMap<String, Object>() {{
                put("default-implicitly-timeout", 30);
                put("service-execution", "<set your device farm ID>");
                put("devices", new LinkedHashMap<String, Object>() {{
                    put("udid", "");
                    put("platformName", "");
                }});
            }});
            put("framework", new LinkedHashMap<String, Object>() {{
                put("screenshot-evidence", new LinkedHashMap<String, Object>() {{
                    put("assertion", "all");
                }});
                put("video-evidence", new LinkedHashMap<String, Object>() {{
                    put("enabled", true);
                    put("assertion", "all");
                    put("localFile", false);
                }});
            }});
        }};
    }

    /**
     * Compara o arquivo de properties existente com o Map default
     * Na necessidade de atualização do arquivo, retorna o updateFile = true
     *
     * @param defaultProps Map de properties Default
     * @param readedProps Arquivo de properties existente
     * @return
     */
    protected boolean compareProperties(Map<String, Object> defaultProps, Map<String, Object> readedProps) {
        for (String key : defaultProps.keySet()) {
            if (!readedProps.containsKey(key)) {
                readedProps.put(key, defaultProps.get(key));
                updateFile = true;
            }
            if (readedProps.get(key) instanceof Map) {
                compareProperties((Map<String, Object>) defaultProps.get(key), (Map<String, Object>) readedProps.get(key));
            }
        }
        return updateFile;
    }
}
