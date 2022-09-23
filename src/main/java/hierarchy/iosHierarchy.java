package hierarchy;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.support.PageFactory;

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
public class iosHierarchy {

    private AppiumDriver<MobileElement> driver;
    private int implicitWait;
    private int explicitWait;
    private boolean exactlyElement = false;
    private boolean immediateReturn = false;
    private boolean ignoreElementException;
    List<ElementIos> elementos = new LinkedList<ElementIos>();

    public iosHierarchy(AppiumDriver<MobileElement> appiumDriver) {
        this.driver = appiumDriver;
        setDefault();
    }

    /**
     * Redefine parametros da classe
     */
    public iosHierarchy setDefault() {
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
    public iosHierarchy ignoreElementException(boolean interruptor) {
        ignoreElementException = interruptor;
        return this;
    }

    /**
     * Permite alteração no tempo de timeout do implicit para buscas por hierarquia
     *
     * @param time Novo tempo para o implicitwait
     * @return Retorna a propria classe
     */
    public iosHierarchy implicitTimeout(int time) {
        implicitWait = time;
        return this;
    }

    /**
     * Define se o elemento a ser procurado precisa ser identico ao enviado por parametro
     *
     * @param interruptor True equivale a equals, false a contains
     * @return Retorna instancia da classe
     */
    public iosHierarchy setExactlyElement(boolean interruptor) {
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
    public iosHierarchy immediateReturn(boolean interruptor) {
        this.immediateReturn = interruptor;
        return this;
    }

    /**
     * Extrai lista de todos os elementos existentes na interface do app, mesmo que não esteja visivel
     *
     * @param type Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public List<String> retriveHierarchyElement(String type, String atributo, String valor) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= implicitWait) {
            PageFactory.initElements(new AppiumFieldDecorator(driver), this);
            List<String> hierarquia = Arrays.asList(driver.getPageSource().split("\n"));
            List<String> elementos = new LinkedList<String>();
            for (String elemento : hierarquia) {
                if (elemento.contains(type) && elemento.contains(atributo) && elemento.contains(valor)) {
                    if (exactlyElement) {
                        ElementIos ele = new ElementIos().processarElemento(elemento);
                        if (ele.elementEquals(atributo, valor) && ele.type.equals(type)) {
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
            if (!elementos.isEmpty()) {
                timer.stop();
                timer.reset();
                return elementos;
            }
        }
        timer.stop();
        timer.reset();
        if (!ignoreElementException)
            throw new NoSuchElementException("Nenhum elemento encontrado em "+implicitWait+" segundos");
        else
            return null;
    }

    /**
     * Extrai lista de todos os elementos existentes na interface do app
     * A partir disso, retorna elementos da sub-hierarquia cujo dentro do elemento indicado por parametro
     *
     * @param type Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public List<List<String>> retriveChildElements(String type, String atributo, String valor) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= implicitWait) {
            PageFactory.initElements(new AppiumFieldDecorator(driver), this);
            List<String> hierarquia = Arrays.asList(driver.getPageSource().split("\n"));
            List<List<String>> subHierarquia = new LinkedList<List<String>>();
            List<String> linhaElemento = new LinkedList<>();
            int contaEspacoBranco = -1;
            for (String elemento : hierarquia) {
                if (elemento.contains(type) && elemento.contains(atributo) && elemento.contains(valor)) {
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
            if (!subHierarquia.isEmpty()) {
                return subHierarquia;
            }
        }
        timer.reset();
        if (!ignoreElementException)
            throw new NoSuchElementException("Nenhum elemento encontrado em "+implicitWait+" segundos");
        else
            return null;
    }

    /**
     * Processa atributos de um elemento e aguarda que o atributo displayed seja true
     *
     * @param type Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param click Verdadeiro ou falso caso queira clicar em um elemento após localizado
     * @return Retorna lista de elementos encontrado
     */
    public boolean waitElementVisibility(String type, String atributo, String valor, boolean click) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(type, atributo, valor);
            for (String lnElemento : linhasElemento) {
                ElementIos elemento = new ElementIos(driver).processarElemento(lnElemento);
                if (elemento.visible) {
                    if (click)
                        elemento.pointClick();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processa atributos de um elemento e aguarda que o atributo clickable seja true
     *
     * @param type Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param click Verdadeiro ou falso caso queira clicar em um elemento após localizado
     * @return Retorna lista de elementos encontrado
     */
    public boolean waitElementClickable(String type, String atributo, String valor, boolean click) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(type, atributo, valor);
            if (linhasElemento == null) {
                return false;
            }
            for (String lnElemento : linhasElemento) {
                ElementIos elemento = new ElementIos(driver).processarElemento(lnElemento);
                if (elemento.enabled) { //FIXME VERIFICAR SE ENABLED OU VISIBLE
                    if (click)
                        elemento.pointClick();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processa atributos de um elemento e aguarda que o atributo apareça e depois suma da hierarquia
     *
     * @param type Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @return Retorna lista de elementos encontrado
     */
    public boolean waitElementStaleness(String type, String atributo, String valor) {
        StopWatch timer = new StopWatch();
        timer.start();
        boolean estadoExcecao = ignoreElementException;
        ignoreElementException(true);
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(type, atributo, valor);
            if (linhasElemento == null || linhasElemento.isEmpty()) {
                throw new RuntimeException("Não é possivel aguardar desaparecimento do elemento, elemento não encontrado");
            }
            for (String lnElemento : linhasElemento) {
                ElementIos elemento = new ElementIos().processarElemento(lnElemento);
                if (elemento.enabled) {
                    break;
                }
            }
        }
        timer.reset();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(type, atributo, valor);
            if (linhasElemento.isEmpty()){
                return true;
            }
        }
        ignoreElementException = estadoExcecao;
        return false;
    }

    /**
     * Realiza uma busca pelo elemento aguarando que seu atributo text tenha o valor alterado
     *
     * @param type Classe do atributo
     * @param atributo Atributo do elemento
     * @param valor Valor referente ao atributo
     * @param textoAtual Se refere ao atributo text
     * @return Veradeiro ou falso a depender se o procedimento foi feito com sucesso
     */
    public boolean waitElementTextChange(String type, String atributo, String valor, String textoAtual) {
        StopWatch timer = new StopWatch();
        timer.start();
        while (timer.getTime(TimeUnit.SECONDS) <= explicitWait) {
            List<String> linhasElemento  = retriveHierarchyElement(type, atributo, valor);
            if (linhasElemento != null) {
                for (String lnElemento : linhasElemento) {
                    ElementIos elemento = new ElementIos().processarElemento(lnElemento);
//                    if (!elemento.text.equals(textoAtual)) {
//                        return true; TODO DESENVOLVER IOS
//                    }
                }
            }
        }
        return false;
    }
    

    /**
     * Realiza um processamento dos atributos de uma linha da hierarquia
     *
     * @param linha Linha de elemento da hierarquia
     * @return Retorna o elemento ja processado
     */
    public ElementIos converterLinha(String linha) {
        return new ElementIos().processarElemento(linha);
    }
    
}
