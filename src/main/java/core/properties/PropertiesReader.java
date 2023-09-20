package core.properties;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Realiza a leitura do arquivo YAML
 */
public class PropertiesReader {

    private static LinkedHashMap<String, Object> yamlProperties;
    private final String yamlPath = "src/main/resources/properties.yml";
    private PropertiesException log = new PropertiesException();

    /**
     * Realiza a leitura do arquivo YAML
     * Caso não exista, solicita a criação do arquivo padrão
     */
    protected void readYaml() {
        if (!new File(yamlPath).exists()) {
            log.info("Arquivo de properties não encontrado, criando novo arquivo.");
            new DefaultProperties().createDefaultYamlProperties();
        }
        StringBuilder yamlText = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(yamlPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                if (!line.replaceAll(" ", "").startsWith("#")) {
                    yamlText.append(line).append("\n");
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Yaml yaml = new Yaml();
            yamlProperties = yaml.load(String.valueOf(yamlText));
        } catch (Exception e) {
            throw new PropertiesException("Error reading properties.yml, check file structure." + "\n"+e);
        }
    }

    /**
     * Recebe as properties
     * @return Retorna o Map de properties
     */
    protected synchronized Map<String, Object> getYamlProperties() {
        if (yamlProperties == null) {
            readYaml();
        }
        return yamlProperties;
    }

    /**
     * Realiza uma nova leitura do arquivo YAML
     * @return Retorna o Map de properties
     */
    protected Map<String, Object> refreshProperties() {
        readYaml();
        return yamlProperties;
    }

}
