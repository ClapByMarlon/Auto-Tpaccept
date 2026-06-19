package de.clapbymarlon.autotpa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TpaRequestParser {
    private static final Pattern COLOR_CODE = Pattern.compile("\u00A7[0-9A-FK-ORa-fk-or]");
    private static final Pattern REQUEST_TO_YOU = Pattern.compile("\u2503\\s*([A-Za-z0-9_]{3,16})(?:\\s+.*?)?\\s+m\u00F6chte\\s+sich\\s+zu\\s+dir\\s+teleportieren\\.", Pattern.CASE_INSENSITIVE);
    private static final Pattern REQUEST_TO_PLAYER = Pattern.compile("\u2503\\s*([A-Za-z0-9_]{3,16})(?:\\s+.*?)?\\s+m\u00F6chte,\\s+dass\\s+du\\s+dich\\s+zu\\s+der\\s+Person\\s+teleportierst\\.", Pattern.CASE_INSENSITIVE);

    public TpaRequest parseRequest(String message) {
        if (message == null) {
            return null;
        }

        String normalized = normalize(message);
        String playerName = find(REQUEST_TO_YOU, normalized);
        if (playerName != null) {
            return new TpaRequest(playerName, TpaRequest.Type.TPA);
        }

        playerName = find(REQUEST_TO_PLAYER, normalized);
        return playerName == null ? null : new TpaRequest(playerName, TpaRequest.Type.TPA_HERE);
    }

    private String find(Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String normalize(String message) {
        return COLOR_CODE.matcher(message).replaceAll("").replace('|', '\u2503').trim();
    }
}
