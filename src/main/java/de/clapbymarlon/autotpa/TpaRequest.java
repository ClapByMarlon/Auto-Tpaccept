package de.clapbymarlon.autotpa;

public final class TpaRequest {
    public enum Type {
        TPA,
        TPA_HERE
    }

    private final String playerName;
    private final Type type;

    public TpaRequest(String playerName, Type type) {
        this.playerName = playerName;
        this.type = type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Type getType() {
        return type;
    }
}
