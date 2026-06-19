package de.clapbymarlon.autotpa;

import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.ListContainerElement;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import java.util.List;

public final class AutoTpaSettings {
    private AutoTpaSettings() {
    }

    public static void fill(final AutoTpaAddon addon, List<SettingsElement> settings) {
        final AutoTpaConfig config = addon.getAutoTpaConfig();

        ListContainerElement general = new ListContainerElement("Allgemein", icon(Material.REDSTONE_COMPARATOR_ON));
        settings.add(general);
        general.getSubSettings().add(new BooleanElement("Aktiviert", icon(Material.LEVER), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                config.setBoolean("enabled", value.booleanValue());
                addon.saveAutoTpaConfig();
            }
        }, config.isEnabled()));
        general.getSubSettings().add(new NumberElement("Accept-Delay ms", icon(Material.WATCH), (int) config.getAcceptDelayMillis()).setMinValue(0).setSteps(50).addCallback(new Consumer<Integer>() {
            @Override
            public void accept(Integer value) {
                config.setLong("acceptDelayMillis", value.longValue());
                addon.saveAutoTpaConfig();
            }
        }));

        ListContainerElement whitelist = new ListContainerElement("Whitelist", icon(Material.SKULL_ITEM));
        settings.add(whitelist);
        for (String playerName : config.getWhitelist()) {
            whitelist.getSubSettings().add(new WhitelistPlayerElement(addon, config, whitelist, playerName));
        }
        whitelist.getSubSettings().add(new AddWhitelistPlayerElement(addon, config, whitelist));
    }

    private static ControlElement.IconData icon(Material material) {
        return new ControlElement.IconData(material);
    }
}
