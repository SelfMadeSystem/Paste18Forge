package uwu.smsgamer.pasteclient.discordrpc;

import net.arikia.dev.drpc.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.*;
import net.minecraftforge.fml.client.GuiModsMissingForServer;
import uwu.smsgamer.pasteclient.PasteClient;

public class RPCManager { // Make customizable via module
    private static RPCManager instance;

    public static RPCManager getInstance() {
        if (instance == null) instance = new RPCManager();
        return instance;
    }

    public void start() {
        DiscordEventHandlers handler = new DiscordEventHandlers.Builder().setReadyEventHandler((a) ->
          System.out.println("DiscordRPC initialized."))
          .build();
        DiscordRPC.discordInitialize("797581586748538911", handler, true);
        DiscordRPC.discordRegister("797581586748538911", "");
        loadingPresence();
    }

    public void loadingPresence() {
        updatePresence("Loading...");
    }

    public void menuPresence() {
        updatePresence("In main menu.");
    }

    public void serverPresence(String serverName) {
        updatePresence("Playing on: " + serverName);
    }

    public void joiningServerPresence() {
        updatePresence("Joining server...");
    }

    public void singlePlayerPresence() {
        updatePresence("Playing in single player.");
    }

    public void updatePresence() {
        if (Minecraft.getMinecraft().isSingleplayer() ||
          (Minecraft.getMinecraft().getCurrentServerData() != null &&
          Minecraft.getMinecraft().getCurrentServerData().isOnLAN())) {
            singlePlayerPresence();
        } else if (Minecraft.getMinecraft().getCurrentServerData() == null) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiModsMissingForServer ||
              Minecraft.getMinecraft().currentScreen instanceof GuiConnecting) {
                joiningServerPresence();
            } else
                menuPresence();
        } else {
            serverPresence(Minecraft.getMinecraft().getCurrentServerData().serverIP);
        }
    }

    public void updatePresence(String text) {
        DiscordRichPresence rich = new DiscordRichPresence.Builder(text)
          .setBigImage("icon", PasteClient.CLIENT_NAME + " v" + PasteClient.CLIENT_VERSION)
          .setSmallImage("icon", "By: " + PasteClient.CLIENT_AUTHOR)
          .build();
        DiscordRPC.discordUpdatePresence(rich);
    }
}
