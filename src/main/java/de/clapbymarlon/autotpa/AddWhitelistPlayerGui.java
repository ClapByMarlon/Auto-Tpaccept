package de.clapbymarlon.autotpa;

import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public final class AddWhitelistPlayerGui extends GuiScreen {
    private final GuiScreen parent;
    private final AutoTpaAddon addon;
    private final AutoTpaConfig config;
    private final SettingsElement container;
    private final AddWhitelistPlayerElement addElement;
    private GuiTextField nameField;
    private String errorMessage = "";

    public AddWhitelistPlayerGui(GuiScreen parent, AutoTpaAddon addon, AutoTpaConfig config) {
        this(parent, addon, config, null, null);
    }

    public AddWhitelistPlayerGui(GuiScreen parent, AutoTpaAddon addon, AutoTpaConfig config, SettingsElement container, AddWhitelistPlayerElement addElement) {
        this.parent = parent;
        this.addon = addon;
        this.config = config;
        this.container = container;
        this.addElement = addElement;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        if (parent != null) {
            parent.width = width;
            parent.height = height;
        }

        nameField = new GuiTextField(0, fontRendererObj, width / 2 - 150, height / 4 + 45, 300, 20);
        nameField.setMaxStringLength(16);
        nameField.setFocused(true);
        buttonList.clear();
        buttonList.add(new GuiButton(2, width / 2 - 105, height / 4 + 85, 100, 20, "Abbrechen"));
        buttonList.add(new GuiButton(1, width / 2 + 5, height / 4 + 85, 100, 20, "Hinzuf\u00FCgen"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        if (parent != null) {
            parent.updateScreen();
        }
        nameField.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            save();
        } else if (button.id == 2) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
            return;
        }

        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            save();
            return;
        }

        nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (parent != null) {
            parent.drawScreen(0, 0, partialTicks);
        } else {
            drawDefaultBackground();
        }
        drawRect(0, 0, width, height, Integer.MIN_VALUE);

        nameField.drawTextBox();
        if (!errorMessage.isEmpty()) {
            drawCenteredString(fontRendererObj, errorMessage, width / 2, height / 4 + 70, 0xFF5555);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void save() {
        String playerName = nameField.getText() == null ? "" : nameField.getText().trim();
        if (config.addWhitelistPlayer(playerName)) {
            addon.saveAutoTpaConfig();
            addVisibleEntry(playerName);
            if (parent != null) {
                parent.initGui();
            }
            mc.displayGuiScreen(parent);
        } else {
            errorMessage = "Ungueltiger Name oder schon vorhanden";
        }
    }

    private void addVisibleEntry(String playerName) {
        if (container == null || container.getSubSettings() == null) {
            return;
        }

        int index = container.getSubSettings().getElements().indexOf(addElement);
        if (index < 0) {
            container.getSubSettings().add(new WhitelistPlayerElement(addon, config, container, playerName));
            return;
        }

        container.getSubSettings().getElements().add(index, new WhitelistPlayerElement(addon, config, container, playerName));
    }
}
