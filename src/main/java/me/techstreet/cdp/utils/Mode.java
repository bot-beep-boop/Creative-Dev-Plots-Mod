package me.techstreet.cdp.utils;

public enum Mode {
    DEV("Dev"),
    BUILD("Build"),
    PLAY("Play"),
    SPAWN("Spawn"),
    IDLE("Idle"),
    UNKNOWN("Unknown");

    private final String identifier;
    public static Mode CURRENT_MODE;

    Mode(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
