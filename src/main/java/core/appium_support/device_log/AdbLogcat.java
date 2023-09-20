package core.appium_support.device_log;

import core.thread.LocalCucumber;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobilePlatform;
import io.cucumber.java.Scenario;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executa a extração de logs ADB Logcat
 *
 * @author bruno.armonico
 */
public class AdbLogcat {

    AppiumDriver driver;
    private Map<String, List<String>> map;
    private final String FILE_PATH = "target/adblog/";
    String buildUrl = System.getenv("BUILD_URL");

    public AdbLogcat(AppiumDriver appiumDriver) {
        this.driver = appiumDriver;
    }

    /**
     * Retira ID do processo do pacote no sistema
     *
     * @param appPackage Nome do pacote
     * @return ID do pacote
     */
    private String getPidof(String appPackage) {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "pidof " + appPackage);
        return driver.executeScript("mobile: shell", args).toString().split("\n")[0];
    }

    /**
     * Start logcat log collector
     */
    public void start() {
        if (driver.getCapabilities().getPlatformName().name().equalsIgnoreCase(MobilePlatform.IOS))
            return;
        driver.manage().logs().get("logcat");
    }

    /**
     * Stop and save logcat search
     * Use platformName capability for search
     *
     * @return This locgcat instance
     */
    public AdbLogcat stop() {
        if (driver.getCapabilities().getPlatformName().name().equalsIgnoreCase(MobilePlatform.IOS))
            return this;
        String appPackage = (String) driver.getCapabilities().getCapability("appPackage");
        String pidof = getPidof(appPackage);
        LogEntries secondCallToLogs = driver.manage().logs().get("logcat");
        map = new HashMap<>();
        for (LogEntry log : secondCallToLogs.getAll()) {
            if (log.getMessage().contains(pidof)) { // Check if logcat match package process ID
                String process = regexProcess(log.getMessage()); //Request batch number
                if (process != null) {
                    map.computeIfAbsent(process, k -> new LinkedList<String>());
                    map.get(process).add(log.getMessage());
                }
            }
        }
        return this;
    }

    /**
     * Stop and save logcat search
     *
     * @param appPackage app package name for search
     *
     * @return This locgcat instance
     */
    public AdbLogcat stop(String appPackage) {
        if (driver.getCapabilities().getPlatformName().name().equalsIgnoreCase(MobilePlatform.IOS))
            return this;
        String pidof = getPidof(appPackage);
        LogEntries secondCallToLogs = driver.manage().logs().get("logcat");
        map = new HashMap<>();
        for (LogEntry log : secondCallToLogs.getAll()) {
            if (log.getMessage().contains(pidof)) { // Check if logcat match package process ID
                String process = regexProcess(log.getMessage()); //Request batch number
                if (process != null) {
                    map.computeIfAbsent(process, k -> new LinkedList<String>());
                    map.get(process).add(log.getMessage());
                }
            }
        }
        return this;
    }

    /**
     * Regex para busca do numero do batch
     *
     * @param elem Linha de console adb logcat
     * @return Numero do batch
     */
    public String regexProcess(String elem) {
        Pattern pat1 = Pattern.compile("([0-9])+(?= I )");
        Matcher mat = pat1.matcher(elem);
        mat.find();
        try {
            return mat.group();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Print in console logcat info
     * Necessary start adb log execution before run
     */
    public void printLog() {
        for (List<String> list : map.values()) {
            System.out.println("----------------------------------------BATCH----------------------------------------");
            for (String linha : list) {
                System.out.println(linha);
            }
        }
    }

    /**
     * Save logcat into file
     */
    public void saveLog() {
        if (driver.getCapabilities().getPlatformName().name().equalsIgnoreCase(MobilePlatform.IOS))
            return;
        File directory = new File(FILE_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
        List<String> ordemPid = sortDataHora();
        Scenario scenario = LocalCucumber.getScenario();
        String testName = removerAcentuacao(scenario.getName() + "_"+scenario.getLine());
        try (PrintWriter out = new PrintWriter(FILE_PATH + testName + ".log")) {
            for (String pid : ordemPid) {
                out.println("--------------------------------------------------------------------------------");
                for (String linha : map.get(pid)) {
                    out.println(linha);
                }
            }
            String urlReport = ""; // TODO: Log for Cucumber Report Jenkins
            if (buildUrl != null)
                urlReport = buildUrl + "Logs/" + testName + ".log";
            else {
                urlReport = "file:///" + System.getProperty("user.dir").replaceAll("\\\\", "/") + "/" + FILE_PATH + testName + ".log";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read PIDs with date and time and re-order
     *
     * @return Ordained PID list
     */
    private List<String> sortDataHora() {
        Map<String, LocalDateTime> listaPrint = new HashMap<>();
        List<LocalDateTime> l = new LinkedList<>();
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendPattern("MM-dd HH:mm:ss.SSS")
                .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear()).toFormatter();
        for (int pids = 0; pids < map.size(); pids++) {
            String key = (String) map.keySet().toArray()[pids];
            String value = map.get(key).get(0);
            // Regex data e hora
            Matcher mat = Pattern.compile("([0-9]+)-([0-9]+) ([0-9]+):([0-9]+):([0-9]+).([0-9]+)").matcher(value);
            mat.find();
            value = mat.group();
            // Conversão para localdatetime
            LocalDateTime dateValue = LocalDateTime.parse(value, parser);
            listaPrint.put(key, dateValue);
            l.add(dateValue);
        }
        l.sort(Comparator.naturalOrder());
        List<String> ordemPid = new LinkedList<>();
        for (LocalDateTime data : l) {
            for (String key : listaPrint.keySet()) {
                if (listaPrint.get(key).toString().equals(data.toString())) {
                    ordemPid.add(key);
                    break;
                }
            }
        }
        return ordemPid;
    }

    private String removerAcentuacao(String texto) {
        texto = texto.replaceAll("ã", "a");
        texto = texto.replaceAll("â", "a");
        texto = texto.replaceAll("á", "a");
        texto = texto.replaceAll("é", "e");
        texto = texto.replaceAll("ê", "e");
        texto = texto.replaceAll("ç", "c");
        texto = texto.replaceAll("í", "i");
        texto = texto.replaceAll("ó", "o");
        texto = texto.replaceAll("õ", "o");
        texto = texto.replaceAll("ô", "o");
        texto = texto.replaceAll(" ", "_");
        return texto;
    }

    /**
     * Retorna Map com logs
     *
     * @return Map com logs
     */
    public Map<String, List<String>> getLogs() {
        return map;
    }
}
