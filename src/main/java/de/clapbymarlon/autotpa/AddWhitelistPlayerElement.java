package de.clapbymarlon.autotpa;

import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;

public final class AddWhitelistPlayerElement extends StandaloneAddEntryElement {
    private final AutoTpaAddon addon;
    private final AutoTpaConfig config;
    private final SettingsElement container;

    public AddWhitelistPlayerElement(AutoTpaAddon addon, AutoTpaConfig config, SettingsElement container) {
        super("Spieler hinzuf\u00FCgen");
        this.addon = addon;
        this.config = config;
        this.container = container;
    }

    @Override
    protected void onClick() {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.displayGuiScreen(new AddWhitelistPlayerGui(minecraft.currentScreen, addon, config, container, this));
    }
}
