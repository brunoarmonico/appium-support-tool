package elements.invisible_element;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe de armazenamento e processamento de atributos do elemento
 * @author bruno.armonico
 */
public class Elemento {
    public String className;
    public int index;
    public String text;
    public boolean checkable;
    public boolean checked;
    public boolean clickable;
    public boolean enabled;
    public boolean focusable;
    public boolean focused;
    public boolean longClickable;
    public boolean password;
    public boolean scrollable;
    public boolean selected;
    public String bounds;
    public boolean displayed;
    public int[] coordenadas;
    public int[] center;

    private Pattern pattern;
    private Matcher match;

    /**
     * Realiza o processamento dos atributos do elemento
     *
     * @param linhaElemento Linha de texto extraida da hierarquia
     * @return elements.invisible_element.Elemento com atributos ja processado
     */
    public Elemento processarElementos(String linhaElemento) {
        regexAtributo(linhaElemento);
        return this;
    }

    /**
     * Extrai o valor de um atributo em especifico
     *
     * @param atributo Atributo a ser realizado regex do valor
     * @param linha Linha de hierarquia a ser processada
     * @return Valor do atributo especificado
     */
    public String regexAtributo(String atributo, String linha) {
        //TODO EM DESENVOLVIMENTO
        pattern = Pattern.compile("(?<="+atributo+"=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        if (match.find()) {
            className = match.group(1);
        } else {
            return null;
        }
        return null;
    }

    /**
     * Extrai valores de atributos usando regex
     *
     * @param linha Linha de texto extraida da hierarquia
     */
    private void regexAtributo(String linha) {
        pattern = Pattern.compile("(?<=class=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        className = match.group(1);

        pattern = Pattern.compile("(?<=index=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        index = Integer.parseInt(match.group(1));

        pattern = Pattern.compile("(?<=text=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        text = match.group(1);

        pattern = Pattern.compile("(?<=checkable=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        checkable = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=checked=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        checked = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=clickable=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        clickable = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=enabled=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        enabled = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=focusable=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        focusable = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=focused=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        focused = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=long-clickable=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        longClickable = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=password=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        password = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=scrollable=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        scrollable = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=selected=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        selected = Boolean.parseBoolean(match.group(1));

        pattern = Pattern.compile("(?<=bounds=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        bounds = match.group(1);

        pattern = Pattern.compile("(?<=displayed=\")(.+?)\"(.)");
        match = pattern.matcher(linha);
        match.find();
        displayed = Boolean.parseBoolean(match.group(1));
        extractCoordinates();
    }

    /** Realiza a extração das coordenadas de um elemento
     * Extração regex sob string [000,000][000,000]
     *
     * @return Vetor com coordenadas [X][Y][X][Y]
     */
    private void extractCoordinates() {
        Pattern pat = Pattern.compile("\\[([-](\\d{1,4})|(\\d{1,4}))?,([-](\\d{1,4})|(\\d{1,4}))\\]");
        Matcher matchBounds = pat.matcher(bounds);
        matchBounds.find();
        String match1 = matchBounds.group();
        matchBounds.find();
        String match2 = matchBounds.group();

        Pattern pat2 = Pattern.compile("(-\\d+)|(\\d+)");
        Matcher coordenada1 = pat2.matcher(match1);
        Matcher coordenada2 = pat2.matcher(match2);


        int[] coordenadas = new int[4];
        coordenada1.find();
        coordenadas[0] = Integer.parseInt(coordenada1.group());
        coordenada1.find();
        coordenadas[1] = Integer.parseInt(coordenada1.group());
        coordenada2.find();
        coordenadas[2] = Integer.parseInt(coordenada2.group());
        coordenada2.find();
        coordenadas[3] = Integer.parseInt(coordenada2.group());
        this.coordenadas = coordenadas;

        int[] centroElemento = new int[2];
        int pontoX = (coordenadas[0] + ((coordenadas[2] - coordenadas[0])/2));
        int pontoY = (coordenadas[1] + ((coordenadas[3] - coordenadas[1])/2));
        centroElemento[0] = pontoX;
        centroElemento[1] = pontoY;
        this.center = centroElemento;
    }

    /**
     * Realiza click no centro da coordenada
     * @param driver Controlador da execução
     */
    public void pointClick(AppiumDriver<MobileElement> driver) {
        extractCoordinates();
        TouchAction action = new TouchAction(driver);
        int pontoX = (coordenadas[0] + ((coordenadas[2] - coordenadas[0])/2));
        int pontoY = (coordenadas[1] + ((coordenadas[3] - coordenadas[1])/2));
        action.tap(PointOption.point(pontoX, pontoY)).perform();
    }

    /**
     * Verifica se o elemento esta presente na tela
     * @param driver Controlador da execução
     * @return Retorna a validação se o elemento esta dentro ou fora da tela
     */
    public boolean isOnScreen(AppiumDriver<MobileElement> driver) {
        Dimension size = driver.manage().window().getSize();
        extractCoordinates();
        int pontoX = (coordenadas[0] + ((coordenadas[2] - coordenadas[0])/2));
        int pontoY = (coordenadas[1] + ((coordenadas[3] - coordenadas[1])/2));
        return ((pontoX < size.width && pontoX > 0) && (pontoY < (size.height - (size.height * 0.05)) && pontoY > 0));
    }
}
