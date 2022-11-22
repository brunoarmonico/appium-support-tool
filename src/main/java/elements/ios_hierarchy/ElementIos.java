package elements.ios_hierarchy;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe de armazenamento e processamento de atributos do elemento
 * @author bruno.armonico
 */
public class ElementIos {

    private AppiumDriver<MobileElement> driver;

    public String type;
    public boolean enabled;
    public boolean visible;
    public String name;
    public String value;
    public String label;
    public int x;
    public int y;
    public int width;
    public int height;

    private Pattern pattern;
    private Matcher match;

    public ElementIos(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }

    public ElementIos() {
    }

    /**
     * Realiza o processamento dos atributos do elemento
     *
     * @param linhaElemento Linha de texto extraida da hierarquia
     * @return elements.invisible_element.Elemento com atributos ja processado
     */
    public ElementIos processarElemento(String linhaElemento) {
        regexAtributo(linhaElemento);
        return this;
    }

    /**
     * Extrai valores de atributos usando regex
     *
     * @param linha Linha de texto extraida da hierarquia
     */
    private void regexAtributo(String linha) {
        pattern = Pattern.compile("(?<=type=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        type = match.group(1);

        pattern = Pattern.compile("(?<=enabled=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        enabled = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=visible=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        visible = Boolean.parseBoolean(match.group(1));


        pattern = Pattern.compile("(?<=name=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        name = match.group(1);

        pattern = Pattern.compile("(?<=value=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        value = match.group(1);

        pattern = Pattern.compile("(?<=label=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        label = match.group(1);

        pattern = Pattern.compile("(?<=x=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        x = Integer.parseInt(match.group(1));

        pattern = Pattern.compile("(?<=y=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        y = Integer.parseInt(match.group(1));

        pattern = Pattern.compile("(?<=width=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        width = Integer.parseInt(match.group(1));

        pattern = Pattern.compile("(?<=height=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        height = Integer.parseInt(match.group(1));
    }

    /**
     * Realiza click no centro do elemento
     */
    public void pointClick() {
        TouchAction action = new TouchAction(driver);
        int pontoX = (x + (width/2));
        int pontoY = (y + (height/2));
        action.tap(PointOption.point(pontoX, pontoY)).perform();
    }

    public boolean elementEquals(String atributo, String valor) {
        switch (atributo){
            case "type":
                if (valor.equals(type))
                    return true;
            case "name":
                if (valor.equals(name))
                    return true;
            case "value":
                if (valor.equals(value))
                    return true;
            case "label":
                if (valor.equals(label))
                    return true;
            default:
                return false;
        }

    }

}
