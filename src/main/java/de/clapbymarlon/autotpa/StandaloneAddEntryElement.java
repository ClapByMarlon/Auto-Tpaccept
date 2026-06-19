package de.clapbymarlon.autotpa;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.ModColor;

/*
 * Standalone adaptation of GrieferUtils' LabyMod 3 EntryAddSettingImpl.
 *
 * Original project: https://github.com/L3g7/GrieferUtils
 * Original license: Apache License 2.0
 */
public abstract class StandaloneAddEntryElement extends ControlElement {
    protected StandaloneAddEntryElement(String name) {
        super(name, new IconData("labymod/textures/settings/category/addons.png"));
        setSettingEnabled(false);
    }

    protected abstract void onClick();

    @Override
    public int getObjectWidth() {
        return 0;
    }

    @Override
    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        mouseOver = mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;

        LabyMod.getInstance().getDrawUtils().drawRectangle(x, y, maxX, maxY, ModColor.toRGB(80, 80, 80, 60));
        int iconWidth = iconData != null ? 25 : 2;
        mc.getTextureManager().bindTexture(iconData.getTextureIcon());

        if (mouseOver) {
            LabyMod.getInstance().getDrawUtils().drawTexture(x + 2D, y + 2D, 256D, 256D, 18D, 18D);
            LabyMod.getInstance().getDrawUtils().drawString(displayName, x + iconWidth + 1D, y + 7D);
        } else {
            LabyMod.getInstance().getDrawUtils().drawTexture(x + 3D, y + 3D, 256D, 256D, 16D, 16D);
            LabyMod.getInstance().getDrawUtils().drawString(displayName, x + iconWidth, y + 7D);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && mouseOver) {
            onClick();
        }
    }
}
