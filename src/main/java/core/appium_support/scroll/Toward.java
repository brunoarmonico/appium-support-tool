package core.appium_support.scroll;

public enum Toward {

    DOWN("down"),
    UP("up"),
    LEFT("left"),
    RIGHT("right");

    private String moviment;

    Toward(String to) {
        this.moviment = to;
    }

    public String getValue() {
        return moviment;
    }

}
