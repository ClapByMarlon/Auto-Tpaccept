package de.clapbymarlon.autotpa;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AutoTpaConfig {
    private final JsonObject json;

    public AutoTpaConfig(JsonObject json) {
        this.json = json == null ? new JsonObject() : json;
        applyDefaults();
    }

    public boolean isEnabled() {
        return getBoolean("enabled", true);
    }

    public boolean isDebugEnabled() {
        return getBoolean("debug", false);
    }

    public long getAcceptDelayMillis() {
        return getLong("acceptDelayMillis", 100L);
    }

    public List<String> getWhitelist() {
        JsonArray array = json.getAsJsonArray("whitelist");
        List<String> names = new ArrayList<String>();
        if (array != null) {
            for (JsonElement element : array) {
                if (element != null && !element.isJsonNull()) {
                    String name = normalizeName(readName(element));
                    if (name != null && !containsIgnoreCase(names, name)) {
                        names.add(name);
                    }
                }
            }
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    public boolean isWhitelisted(String playerName) {
        String normalized = normalizeName(playerName);
        return normalized != null && containsIgnoreCase(getWhitelist(), normalized);
    }

    public boolean isAllowed(String playerName, TpaRequest.Type type) {
        String normalized = normalizeName(playerName);
        if (normalized == null) {
            return false;
        }

        JsonObject entry = getEntry(normalized);
        if (entry == null) {
            return false;
        }

        if (type == TpaRequest.Type.TPA_HERE) {
            return getEntryBoolean(entry, "tpahere", true);
        }

        return getEntryBoolean(entry, "tpa", true);
    }

    public boolean isTpaAllowed(String playerName) {
        JsonObject entry = getEntry(playerName);
        return entry != null && getEntryBoolean(entry, "tpa", true);
    }

    public boolean isTpaHereAllowed(String playerName) {
        JsonObject entry = getEntry(playerName);
        return entry != null && getEntryBoolean(entry, "tpahere", true);
    }

    public void setTpaAllowed(String playerName, boolean value) {
        updatePlayerFlag(playerName, "tpa", value);
    }

    public void setTpaHereAllowed(String playerName, boolean value) {
        updatePlayerFlag(playerName, "tpahere", value);
    }

    public boolean addWhitelistPlayer(String playerName) {
        String normalized = normalizeName(playerName);
        if (normalized == null || isWhitelisted(normalized)) {
            return false;
        }

        JsonArray array = json.getAsJsonArray("whitelist");
        JsonObject entry = new JsonObject();
        entry.addProperty("name", normalized);
        entry.addProperty("tpa", true);
        entry.addProperty("tpahere", true);
        array.add(entry);
        return true;
    }

    public boolean removeWhitelistPlayer(String playerName) {
        String normalized = normalizeName(playerName);
        if (normalized == null) {
            return false;
        }

        JsonArray array = json.getAsJsonArray("whitelist");
        JsonArray updated = new JsonArray();
        boolean removed = false;
        for (JsonElement element : array) {
            String name = normalizeName(readName(element));
            if (name != null && name.equalsIgnoreCase(normalized)) {
                removed = true;
            } else {
                updated.add(element);
            }
        }

        if (removed) {
            json.add("whitelist", updated);
        }
        return removed;
    }

    public void setBoolean(String key, boolean value) {
        json.addProperty(key, value);
    }

    public void setLong(String key, long value) {
        json.addProperty(key, value);
    }

    private void setWhitelist(List<String> names) {
        JsonArray array = new JsonArray();
        for (String name : names) {
            String normalized = normalizeName(name);
            if (normalized != null && !containsJsonIgnoreCase(array, normalized)) {
                JsonObject entry = new JsonObject();
                entry.addProperty("name", normalized);
                entry.addProperty("tpa", true);
                entry.addProperty("tpahere", true);
                array.add(entry);
            }
        }
        json.add("whitelist", array);
    }

    private void applyDefaults() {
        addDefault("enabled", true);
        addDefault("debug", false);
        addDefault("acceptDelayMillis", 100L);
        if (!json.has("whitelist") || !json.get("whitelist").isJsonArray()) {
            json.add("whitelist", new JsonArray());
        }
    }

    private JsonObject getEntry(String playerName) {
        String normalized = normalizeName(playerName);
        if (normalized == null) {
            return null;
        }

        JsonArray array = json.getAsJsonArray("whitelist");
        JsonArray migratedArray = null;
        for (JsonElement element : array) {
            String name = normalizeName(readName(element));
            if (name != null && name.equalsIgnoreCase(normalized)) {
                if (element.isJsonObject()) {
                    return element.getAsJsonObject();
                }

                JsonObject migrated = new JsonObject();
                migrated.addProperty("name", name);
                migrated.addProperty("tpa", true);
                migrated.addProperty("tpahere", true);
                migratedArray = migrateArray(array, element, migrated);
                json.add("whitelist", migratedArray);
                return migrated;
            }
        }

        return null;
    }

    private void updatePlayerFlag(String playerName, String key, boolean value) {
        JsonObject entry = getEntry(playerName);
        if (entry != null) {
            entry.addProperty(key, value);
        }
    }

    private static JsonArray migrateArray(JsonArray source, JsonElement oldElement, JsonObject newElement) {
        JsonArray migrated = new JsonArray();
        boolean replaced = false;
        for (JsonElement element : source) {
            if (!replaced && element == oldElement) {
                migrated.add(newElement);
                replaced = true;
            } else {
                migrated.add(element);
            }
        }
        return migrated;
    }

    private static String readName(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            return object.has("name") ? object.get("name").getAsString() : null;
        }

        return element.getAsString();
    }

    private static boolean getEntryBoolean(JsonObject entry, String key, boolean fallback) {
        return entry.has(key) ? entry.get(key).getAsBoolean() : fallback;
    }

    private void addDefault(String key, boolean value) {
        if (!json.has(key)) {
            json.addProperty(key, value);
        }
    }

    private void addDefault(String key, long value) {
        if (!json.has(key)) {
            json.addProperty(key, value);
        }
    }

    private boolean getBoolean(String key, boolean fallback) {
        return json.has(key) ? json.get(key).getAsBoolean() : fallback;
    }

    private long getLong(String key, long fallback) {
        return json.has(key) ? json.get(key).getAsLong() : fallback;
    }

    private static String normalizeName(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (!trimmed.matches("[A-Za-z0-9_]{3,16}")) {
            return null;
        }

        return trimmed;
    }

    private static boolean containsIgnoreCase(List<String> names, String name) {
        for (String current : names) {
            if (current.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsJsonIgnoreCase(JsonArray array, String name) {
        for (JsonElement element : array) {
            if (element != null && !element.isJsonNull() && name.equalsIgnoreCase(readName(element))) {
                return true;
            }
        }
        return false;
    }
}
