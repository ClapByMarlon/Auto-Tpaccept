package de.clapbymarlon.autotpa;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class WhitelistPlayerElement extends StandaloneListEntryElement {
    private final AutoTpaAddon addon;
    private final AutoTpaConfig config;
    private final String playerName;
    private final ItemStack skull;

    public WhitelistPlayerElement(AutoTpaAddon addon, AutoTpaConfig config, SettingsElement container, String playerName) {
        super(container, true, true);
        this.addon = addon;
        this.config = config;
        this.playerName = playerName;
        this.skull = createPlayerSkull(playerName);

        getSubSettings().add(new BooleanElement("TPA", icon(Material.LEVER), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                config.setTpaAllowed(playerName, value.booleanValue());
                addon.saveAutoTpaConfig();
            }
        }, config.isTpaAllowed(playerName)));

        getSubSettings().add(new BooleanElement("TPAHere", icon(Material.LEVER), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                config.setTpaHereAllowed(playerName, value.booleanValue());
                addon.saveAutoTpaConfig();
            }
        }, config.isTpaHereAllowed(playerName)));
    }

    @Override
    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        setDisplayName("      " + playerName);
        super.draw(x, y, maxX, maxY, mouseX, mouseY);
        LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, ModColor.toRGB(120, 120, 120, 120));
        LabyMod.getInstance().getDrawUtils().drawItem(skull, x + 3D, y + 3D, null);
    }

    @Override
    public void drawDescription(int mouseX, int mouseY, int screenWidth) {
    }

    @Override
    protected void onDelete() {
        if (config.removeWhitelistPlayer(playerName)) {
            addon.saveAutoTpaConfig();
        }
    }

    private static ControlElement.IconData icon(Material material) {
        return new ControlElement.IconData(material);
    }

    private static ItemStack createPlayerSkull(String playerName) {
        ItemStack skull = new ItemStack(Items.skull, 1, 3);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("SkullOwner", playerName);
        skull.setTagCompound(tag);
        return skull;
    }
}
