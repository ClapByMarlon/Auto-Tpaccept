package de.clapbymarlon.autotpa;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public final class AutoTpaAddon extends LabyModAddon {
    private AutoTpaConfig config;
    private TpaRequestParser parser = new TpaRequestParser();
    private long acceptAtMillis;

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void loadConfig() {
        this.config = new AutoTpaConfig(getConfig());
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        AutoTpaSettings.fill(this, subSettings);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (config == null || !config.isEnabled() || event.message == null) {
            return;
        }

        String message = event.message.getUnformattedText();
        TpaRequest request = parser.parseRequest(message);
        if (request == null) {
            return;
        }

        String playerName = request.getPlayerName();
        if (!config.isAllowed(playerName, request.getType())) {
            debug("TPA ignoriert: " + playerName + " ist nicht erlaubt.");
            return;
        }

        acceptAtMillis = System.currentTimeMillis() + config.getAcceptDelayMillis();
        debug("TPA akzeptiere in " + config.getAcceptDelayMillis() + " ms: " + playerName);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || acceptAtMillis <= 0L) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now < acceptAtMillis) {
            return;
        }

        acceptAtMillis = 0L;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            player.sendChatMessage("/tpaccept");
        }
    }

    public AutoTpaConfig getAutoTpaConfig() {
        if (config == null) {
            config = new AutoTpaConfig(getConfig());
        }
        return config;
    }

    public void saveAutoTpaConfig() {
        saveConfig();
    }

    private void debug(String message) {
        if (config == null || !config.isDebugEnabled()) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            player.addChatMessage(new ChatComponentText("[AutoTPA] " + message));
        }
    }
}
