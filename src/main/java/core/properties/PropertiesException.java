package core.properties;

public class PropertiesException extends RuntimeException {

    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";

    public PropertiesException(String message) {
        super(message);
    }

    public PropertiesException(String message, Exception e) {
        super(message, e);
    }

    public PropertiesException() {}


    public void info(String message) {
        System.out.println(ANSI_YELLOW + "[INFO] " + ANSI_RESET + message);
    }

}
