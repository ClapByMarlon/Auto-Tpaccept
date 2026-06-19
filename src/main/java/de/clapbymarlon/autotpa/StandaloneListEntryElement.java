package de.clapbymarlon.autotpa;

import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;

/*
 * Standalone adaptation of GrieferUtils' LabyMod 3 ListEntrySetting.
 *
 * Original project: https://github.com/L3g7/GrieferUtils
 * Original license: Apache License 2.0
 */
public abstract class StandaloneListEntryElement extends ControlElement {
    private static final ResourceLocation DELETE_ICON = new ResourceLocation("labymod/textures/misc/blocked.png");

    protected SettingsElement container;
    private final boolean deletable;
    private final boolean editable;
    private boolean hoveringDelete;
    private boolean hoveringEdit;
    private int lastX;
    private int lastY;
    private int lastMaxX;
    private int lastMaxY;

    protected StandaloneListEntryElement(SettingsElement container, boolean deletable) {
        this(container, deletable, false);
    }

    protected StandaloneListEntryElement(SettingsElement container, boolean deletable, boolean editable) {
        super("\u00A7f", null);
        this.container = container;
        this.deletable = deletable;
        this.editable = editable;
        setSettingEnabled(editable);
    }

    protected abstract void onDelete();

    @SuppressWarnings("unchecked")
    protected void openSettings() {
        Object screen = Minecraft.getMinecraft().currentScreen;
        if (screen == null || !"net.labymod.settings.LabyModAddonsGui".equals(screen.getClass().getName())) {
            return;
        }

        try {
            Field pathField = screen.getClass().getDeclaredField("path");
            pathField.setAccessible(true);
            ((ArrayList<SettingsElement>) pathField.get(screen)).add(this);

            Minecraft.getMinecraft().currentScreen.initGui();
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Override
    public int getObjectWidth() {
        return editable ? 22 : 0;
    }

    protected void remove() {
        if (container != null && container.getSubSettings() != null) {
            container.getSubSettings().getElements().remove(this);
        }
        onDelete();
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0 || !isInsideLastRow(mouseX, mouseY)) {
            return;
        }

        if (editable && isEditClick(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1));
            openSettings();
            return;
        }

        if (deletable && isDeleteClick(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1));
            remove();
            return;
        }

    }

    @Override
    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        lastX = x;
        lastY = y;
        lastMaxX = maxX;
        lastMaxY = maxY;

        hideSubListButton();
        super.draw(x, y, maxX, maxY, mouseX, mouseY);

        mouseOver = mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY;
        if (!mouseOver) {
            hoveringDelete = false;
            hoveringEdit = false;
        }

        int xPosition = editable ? maxX - 43 : maxX - 20;
        double yPosition = y + 4.5D;

        if (deletable) {
            xPosition = maxX - 22;
            yPosition = y + 3D;
            hoveringDelete = mouseOver && mouseX >= xPosition && mouseY >= yPosition && mouseX <= xPosition + 16D && mouseY <= yPosition + 16D;

            mc.getTextureManager().bindTexture(DELETE_ICON);
            GlStateManager.color(1F, 1F, 1F, 1F);
            int size = hoveringDelete ? 16 : 14;
            int drawX = maxX - 22 + (hoveringDelete ? 0 : 1);
            int drawY = y + (hoveringDelete ? 3 : 4);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            LabyMod.getInstance().getDrawUtils().drawTexture(drawX, drawY, 256D, 256D, size, size);
        }

        if (editable) {
            int gearX = maxX - 43;
            int gearY = y + 4;
            hoveringEdit = mouseOver && mouseX >= gearX && mouseY >= gearY && mouseX <= gearX + 16D && mouseY <= gearY + 16D;

            mc.getTextureManager().bindTexture(ModTextures.BUTTON_ADVANCED);
            LabyMod.getInstance().getDrawUtils().drawTexture(gearX + (hoveringEdit ? -1D : 0D), gearY + (hoveringEdit ? -1D : 0D), 0D, 0D, 256D, 256D, hoveringEdit ? 16D : 14D, hoveringEdit ? 16D : 14D);
        }
    }

    private boolean isInsideLastRow(int mouseX, int mouseY) {
        return mouseX > lastX && mouseX < lastMaxX && mouseY > lastY && mouseY < lastMaxY;
    }

    private boolean isDeleteClick(int mouseX, int mouseY) {
        int xPosition = lastMaxX - 22;
        double yPosition = lastY + 3D;
        return mouseX >= xPosition && mouseY >= yPosition && mouseX <= xPosition + 16D && mouseY <= yPosition + 16D;
    }

    private boolean isEditClick(int mouseX, int mouseY) {
        int xPosition = lastMaxX - 43;
        double yPosition = lastY + 4D;
        return mouseX >= xPosition && mouseY >= yPosition && mouseX <= xPosition + 16D && mouseY <= yPosition + 16D;
    }
}
