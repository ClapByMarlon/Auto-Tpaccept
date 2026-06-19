package de.clapbymarlon.autotpa;

import net.labymod.main.LabyMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.util.List;

public final class AutoTpaWhitelistGui extends GuiScreen {
    private static final int ROW_HEIGHT = 34;

    private final GuiScreen parent;
    private final AutoTpaAddon addon;
    private final AutoTpaConfig config;
    private GuiButton addButton;
    private GuiButton backButton;

    public AutoTpaWhitelistGui(GuiScreen parent, AutoTpaAddon addon, AutoTpaConfig config) {
        this.parent = parent;
        this.addon = addon;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int centerX = width / 2;
        addButton = new GuiButton(1, centerX - 105, height - 34, 130, 20, "+ Spieler hinzufügen");
        backButton = new GuiButton(2, centerX + 30, height - 34, 75, 20, "Zurück");
        buttonList.add(addButton);
        buttonList.add(backButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            mc.displayGuiScreen(new AddWhitelistPlayerGui(this, addon, config));
        } else if (button.id == 2) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && removeClickedPlayer(mouseX, mouseY)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int listX = width / 2 - 210;
        int listY = 42;
        int listWidth = 420;
        int listBottom = height - 45;

        drawCenteredString(fontRendererObj, "Auto TPA Whitelist", width / 2, 18, 0xFFFFFF);
        drawRect(listX - 4, listY - 4, listX + listWidth + 4, listBottom, 0x90000000);

        List<String> players = config.getWhitelist();
        if (players.isEmpty()) {
            drawCenteredString(fontRendererObj, "Keine Spieler eingetragen", width / 2, listY + 18, 0xAAAAAA);
        }

        int y = listY;
        for (String playerName : players) {
            if (y + ROW_HEIGHT > listBottom) {
                break;
            }

            drawPlayerRow(playerName, listX, y, listWidth, mouseX, mouseY);
            y += ROW_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawPlayerRow(String playerName, int x, int y, int width, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + ROW_HEIGHT;
        drawRect(x, y, x + width, y + ROW_HEIGHT - 2, hovered ? 0x804A4A4A : 0x60333333);

        LabyMod.getInstance().getDrawUtils().drawItem(createPlayerSkull(playerName), x + 8D, y + 8D, null);
        drawString(fontRendererObj, playerName, x + 36, y + 12, 0xFFFFFF);

        if (hovered) {
            int removeX = x + width - 32;
            int removeY = y + 6;
            boolean removeHovered = mouseX >= removeX && mouseX <= removeX + 22 && mouseY >= removeY && mouseY <= removeY + 22;
            drawRect(removeX, removeY, removeX + 22, removeY + 22, removeHovered ? 0xE0DD2222 : 0xB0881111);
            drawCenteredString(fontRendererObj, "X", removeX + 11, removeY + 7, 0xFFFFFF);
        }
    }

    private boolean removeClickedPlayer(int mouseX, int mouseY) {
        int listX = width / 2 - 210;
        int listY = 42;
        int listWidth = 420;
        int listBottom = height - 45;

        List<String> players = config.getWhitelist();
        int y = listY;
        for (String playerName : players) {
            if (y + ROW_HEIGHT > listBottom) {
                break;
            }

            int removeX = listX + listWidth - 32;
            int removeY = y + 6;
            if (mouseX >= removeX && mouseX <= removeX + 22 && mouseY >= removeY && mouseY <= removeY + 22) {
                if (config.removeWhitelistPlayer(playerName)) {
                    addon.saveAutoTpaConfig();
                    initGui();
                }
                return true;
            }
            y += ROW_HEIGHT;
        }

        return false;
    }

    private static ItemStack createPlayerSkull(String playerName) {
        ItemStack skull = new ItemStack(Items.skull, 1, 3);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("SkullOwner", playerName);
        skull.setTagCompound(tag);
        return skull;
    }
}
