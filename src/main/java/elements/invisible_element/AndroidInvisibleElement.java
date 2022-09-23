package elements.invisible_element;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Manipulação de elementos com base em atributos extraidos do DOM da pagina
 * @author bruno.armonico
 */
public class AndroidInvisibleElement {

    private AppiumDriver<MobileElement> driver;
    private int implicitWait;
    private int explicitWait;
    private boolean exactlyElement = false;
    private boolean immediateReturn = false;
    private boolean ignoreElementException;
    List<Elemento> elementos = new LinkedList<Elemento>();

    public AndroidInvisibleElement(AppiumDriver<MobileElement> appiumDriver) {
        this.driver = appiumDriver;
        setDefault();
    }

    /**
     * Redefine parametros da classe
     */
    public AndroidInvisibleElement setDefault() {
        this.implicitWait = 30;
        this.explicitWait = 30;
        this.ignoreElementException = false;
        return this;
    }

    /**
     * Altera a condição da ativação de exceção da busca por elementos
     * @param interruptor Verdadeiro ou falso caso queira desligar a exceção de elemento não encontrado
     *
     * @return Retorna a propria classe
     */
    public AndroidInvisibleElement ignoreElementException(boolean interruptor) {
        ignoreElementException = interruptor;
        return this;
    }

    /**
     * Permite alteração no tempo de timeout do implicit para buscas por hierarquia
     *
     * @param time Novo tempo para o implicitwait
     * @return Retorna a propria classe
     */
    public AndroidInvisibleElement implicitTimeout(int time) {
        implicitWait = time;
        return this;
    }

    /**
     * Define se o elemento a ser procurado precisa ser identico ao enviado por parametro
     *
     * @param interruptor True equivale a equals, false a contains
     * @return Retorna instancia da classe
     */
    public AndroidInvisibleElement setExactlyElement(boolean interruptor) {
        exactlyElement = interruptor;
        return this;
    }

    /**
     * Permite que seja retornado o primeiro elemento relacionado encontrado
     * Pode diminuir o tempo de busca em caso de grandes hierarquias
     *
     * @param interruptor Caso verdadeiro a busca por elemento irá retornar sempre um unico elemento
     * @return Retorna a propria classe
     */
    public AndroidInvisibleElement immediateReturn(boolean interruptor) {
        this.immediateReturn = interruptor;
        return this;
    }

    /**
     * Extrai lista de todos os elementos existentes na interface do app, mesmo que não esteja visivel
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public List<String> retriveHierarchyElement(String className, String atributo, String valor) {
        ((AndroidDriver) driver).allowInvisibleElements(true);
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= implicitWait) {
            PageFactory.initElements(new AppiumFieldDecorator(driver), this);
            List<String> hierarquia = Arrays.asList(driver.getPageSource().split("\n"));
            List<String> elementos = new LinkedList<String>();
            for (String elemento : hierarquia) {
                if (elemento.contains(className) && elemento.contains(atributo) && elemento.contains(valor)) {
                    if (exactlyElement) {
                        Elemento ele = new Elemento().processarElementos(elemento);
                        if (ele.text.equals(valor) && ele.className.equals(className)) {
                            elementos.add(elemento);
                        }
                    } else {
                        elementos.add(elemento);
                    }
                    if (immediateReturn) {
                        break;
                    }
                }
            }
            if (!elementos.isEmpty()){
                timer.stop();
                timer.reset();
                return elementos;
            }
        }
        timer.stop();
        timer.reset();
        ((AndroidDriver) driver).allowInvisibleElements(false);
        if (!ignoreElementException)
            throw new NoSuchElementException("Nenhum elemento encontrado em "+implicitWait+" segundos");
        else
            return null;
    }

    /**
     * Extrai lista de todos os elementos existentes na interface do app, mesmo que não esteja visivel
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param regex Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public Elemento retriveHierarchyElementRegex(String className, String atributo, String regex) {
        ((AndroidDriver) driver).allowInvisibleElements(true);
        StopWatch implicityTimer = new StopWatch();
        implicityTimer.start();
        while (implicityTimer.getTime(TimeUnit.SECONDS) <= implicitWait) {
            PageFactory.initElements(new AppiumFieldDecorator(driver), this);
            List<String> hierarquia = Arrays.asList(driver.getPageSource().split("\n"));
            for (String lnHierarquia : hierarquia) {
                if (lnHierarquia.contains(className)) {
                    String valorAtributo = new Elemento().regexAtributo(atributo, lnHierarquia);
                    if (valorAtributo != null) {
                        Matcher match = Pattern.compile(regex).matcher(valorAtributo);
                        if (match.find()) {
                            implicityTimer.stop();
                            implicityTimer.reset();
                            return new Elemento().processarElementos(lnHierarquia);
                        }
                    }
                }
            }
        }
        implicityTimer.stop();
        implicityTimer.reset();
        ((AndroidDriver) driver).allowInvisibleElements(false);
        if (!ignoreElementException)
            throw new NoSuchElementException("Nenhum elemento encontrado em "+implicitWait+" segundos");
        else
            return null;
    }

    /**
     * Extrai lista de todos os elementos existentes na interface do app, mesmo que não esteja visivel
     * A partir disso, retorna elementos da sub-hierarquia cujo dentro do elemento indicado por parametro
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public List<List<String>> retriveChildElements(String className, String atributo, String valor) {
        ((AndroidDriver) driver).allowInvisibleElements(true);
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= implicitWait) {
            PageFactory.initElements(new AppiumFieldDecorator(driver), this);
            List<String> hierarquia = Arrays.asList(driver.getPageSource().split("\n"));
            List<List<String>> subHierarquia = new LinkedList<List<String>>();
            List<String> linhaElemento = new LinkedList<>();
            int contaEspacoBranco = -1;
            for (String elemento : hierarquia) {
                if (elemento.contains(className) && elemento.contains(atributo) && elemento.contains(valor)) {
                    Pattern pattern = Pattern.compile("[\\s]+(?=<)");
                    Matcher match = pattern.matcher(elemento);
                    if (match.find()) {
                        contaEspacoBranco = match.group().length();
                        linhaElemento.add(elemento);
                    }
                }
                if (contaEspacoBranco != -1) {
                    Pattern pattern = Pattern.compile("[\\s]+(?=<)");
                    Matcher match = pattern.matcher(elemento);
                    if (match.find()) {
                        if (match.group().length() > contaEspacoBranco) {
                            linhaElemento.add(elemento);
                        } else if ((match.group().length() == contaEspacoBranco) && (elemento.contains("/"))) {
                            subHierarquia.add(linhaElemento);
                            linhaElemento = new LinkedList<String>();
                            contaEspacoBranco = -1;
                        }
                    }
                }
            }
            if (!subHierarquia.isEmpty()){
                return subHierarquia;
            }
        }
        timer.reset();
        ((AndroidDriver) driver).allowInvisibleElements(false);
        if (!ignoreElementException)
            throw new NoSuchElementException("Nenhum elemento encontrado em "+implicitWait+" segundos");
        else
            return null;
    }

    /**
     * Processa atributos de um elemento e aguarda que o atributo displayed seja true
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param click Verdadeiro ou falso caso queira clicar em um elemento após localizado
     * @return Retorna lista de elementos encontrado
     */
    public boolean waitElementVisibility(String className, String atributo, String valor, boolean click) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(className, atributo, valor);
            for (String lnElemento : linhasElemento) {
                if (new Elemento().processarElementos(lnElemento).displayed) {
                    if (click)
                        extrairCoordenadas(lnElemento, click);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processa atributos de um elemento e aguarda que o atributo clickable seja true
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param click Verdadeiro ou falso caso queira clicar em um elemento após localizado
     * @return Retorna lista de elementos encontrado
     */
    public boolean waitElementClickable(String className, String atributo, String valor, boolean click) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(className, atributo, valor);
            if (linhasElemento == null) {
                return false;
            }
            for (String lnElemento : linhasElemento) {
                Elemento elemento = new Elemento().processarElementos(lnElemento);
                if (elemento.clickable) {
                    if (click)
                        extrairCoordenadas(lnElemento, click);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processa atributos de um elemento e aguarda que o atributo apareça e depois suma da hierarquia
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public boolean waitElementStaleness(String className, String atributo, String valor) {
        StopWatch timer = new StopWatch();
        timer.start();
        boolean estadoExcecao = ignoreElementException;
        ignoreElementException(true);
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(className, atributo, valor);
            if (linhasElemento == null || linhasElemento.isEmpty()) {
                throw new RuntimeException("Não é possivel aguardar desaparecimento do elemento, elemento não encontrado");
            }
            for (String lnElemento : linhasElemento) {
                Elemento elemento = new Elemento().processarElementos(lnElemento);
                if (elemento.displayed) {
                    break;
                }
            }
        }
        timer.reset();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(className, atributo, valor);
            if (linhasElemento.isEmpty()){
                return true;
            }
        }
        ignoreElementException = estadoExcecao;
        return false;
    }

    /**
     * Realiza uma busca pelo elemento aguarando que seu atributo text tenha o valor alterado
     * Utiliza a capability allowInvisibleElements para a extração, por isso pode demorar.
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param textoAtual Se refere ao atributo text
     * @return Veradeiro ou falso a depender se o procedimento foi feito com sucesso
     */
    public boolean waitElementTextChange(String className, String atributo, String valor, String textoAtual) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(className, atributo, valor);
            if (linhasElemento != null) {
                for (String lnElemento : linhasElemento) {
                    Elemento elemento = new Elemento().processarElementos(lnElemento);
                    if (!elemento.text.equals(textoAtual)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Realiza o scroll até que o elemento esteja visivel na tela
     * Primeiro realiza scroll de posicionamento vertical e depois horizontal
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param porcentagemScroll Porcentagem da tela que será movida na execução do scroll
     *
     * @return Retorna elemento processado
     */
    public Elemento moveToElement(String className, String atributo, String valor, int velocidade, double porcentagemScroll) {
        int proporcaoX = driver.manage().window().getSize().getWidth();
        int proporcaoY = driver.manage().window().getSize().getHeight();

        retriveHierarchyElement(className, atributo, valor);
        StopWatch timer = new StopWatch();
        timer.start();
        boolean scrollY = false; // Scroll vertical
        while (!scrollY && timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            int elementoY = converterLinha(retriveHierarchyElement(className, atributo, valor).get(0)).center[1];
            if (elementoY > 0 && elementoY < (proporcaoY * 0.85)) {
                scrollY = true;
            } else {
                if (elementoY > 0) { // Scroll Down
                    new TouchAction(driver).press(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * ((porcentagemScroll / 100) + 1)))).
                            waitAction(WaitOptions.waitOptions(Duration.ofMillis(velocidade))).
                            moveTo(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * (porcentagemScroll / 100)))).
                            release().perform();
                } else { // Scroll Up
                    new TouchAction(driver).press(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * (porcentagemScroll / 100)))).
                            waitAction(WaitOptions.waitOptions(Duration.ofMillis(velocidade))).
                            moveTo(PointOption.point((proporcaoX / 2), (int) (((double) proporcaoY / 2) * ((porcentagemScroll / 100) + 1)))).
                            release().perform();
                }
            }
        }
        boolean scrollX = false; // Scroll horizontal
        while (!scrollX && timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            Elemento element = converterLinha(retriveHierarchyElement(className, atributo, valor).get(0));
            int elementoX = element.center[0];
            int elementoY = element.center[1];
            if (elementoX > 0 && elementoX < (proporcaoY * 0.85)) {
                return element;
            } else {
                if (elementoX > 0) { // Scroll Down
                    new TouchAction(driver).press(PointOption.point((int) (((double) proporcaoX / 2) * ((porcentagemScroll / 100) + 1)), elementoY)).
                            waitAction(WaitOptions.waitOptions(Duration.ofMillis(velocidade))).
                            moveTo(PointOption.point((int) (((double) proporcaoX / 2) * (porcentagemScroll / 100)), elementoY)).
                            release().perform();
                } else { // Scroll Up
                    new TouchAction(driver).press(PointOption.point((int) (((double) proporcaoX / 2) * (porcentagemScroll / 100)), elementoY)).
                            waitAction(WaitOptions.waitOptions(Duration.ofMillis(velocidade))).
                            moveTo(PointOption.point((int) (((double) proporcaoX / 2) * ((porcentagemScroll / 100) + 1)), elementoY)).
                            release().perform();
                }
            }
        }
        throw new RuntimeException("Não foi possivel visualizar o elemento em "+explicitWait+" segundos");
    }

    /** Realiza o scroll até que o elemento esteja visivel na tela
     * Primeiro realiza scroll de posicionamento vertical e depois horizontal
     *
     * @param className Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna elemento processado
     */
    public Elemento moveToElement(String className, String atributo, String valor) {
        return moveToElement(className, atributo, valor, 700, 50);
    }

    /**
     * Realiza um processamento dos atributos de uma linha da hierarquia
     *
     * @param linha Linha de elemento da hierarquia
     * @return Retorna o elemento ja processado
     */
    public Elemento converterLinha(String linha) {
        return new Elemento().processarElementos(linha);
    }

    /** Realiza a extração das coordenadas de um elemento
     * Extração regex sob string [000,000][000,000]
     *
     * @param elemento Linha de elemento extraido da hierarquia a ser utilizado
     * @return Vetor com coordenadas [X][Y][X][Y]
     */
    public int[] extrairCoordenadas(String elemento, boolean click) {
        Pattern pat = Pattern.compile("\\[([0-9]\\d+)?,([0-9]\\d+)\\]");
        Matcher matchBounds = pat.matcher(elemento);
        matchBounds.find();
        String match1 = matchBounds.group();
        matchBounds.find();
        String match2 = matchBounds.group();

        Pattern pat2 = Pattern.compile("\\d+");
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
        if (click)
            clickEmArea(coordenadas);
        return coordenadas;
    }

    /** Realiza click no centro da coordenada
     *
     * @param coordenadas Coordenadas do elemento
     */
    public void clickEmArea(int[] coordenadas) {
        int pontoX = (coordenadas[0] + ((coordenadas[2] - coordenadas[0])/2));
        int pontoY = (coordenadas[1] + ((coordenadas[3] - coordenadas[1])/2));
        new TouchAction(driver).tap(PointOption.point(pontoX, pontoY)).perform();
        System.out.println("Click por coordenada efetuado X "+pontoX + " Y "+pontoY);
    }
}
