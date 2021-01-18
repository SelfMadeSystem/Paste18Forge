package uwu.smsgamer.pasteclient.events.forgevents;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import uwu.smsgamer.pasteclient.discordrpc.RPCManager;

import java.lang.reflect.*;
import java.util.*;

public class ForgeEventHandler {
    private Timer t = new Timer();

    private static ForgeEventHandler instance;
    public static ForgeEventHandler getInstance() {
      if (instance == null) instance = new ForgeEventHandler();
      return instance;
    }

    private ForgeEventHandler() {
        Method registerMethod;
        try { // Ez hack kuz I have no mod
            registerMethod = EventBus.class.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
            Class<?> clazz = FMLNetworkEvent.ClientConnectedToServerEvent.class;

            registerMethod.setAccessible(true);

            registerMethod.invoke(MinecraftForge.EVENT_BUS, clazz, this,
              ForgeEventHandler.class.getMethod("connect", FMLNetworkEvent.ClientConnectedToServerEvent.class),
              Loader.instance().getMinecraftModContainer());

            clazz = FMLNetworkEvent.ClientDisconnectionFromServerEvent.class;

            registerMethod.invoke(MinecraftForge.EVENT_BUS, clazz, this,
              ForgeEventHandler.class.getMethod("disconnect", FMLNetworkEvent.ClientDisconnectionFromServerEvent.class),
              Loader.instance().getMinecraftModContainer());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void connect(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RPCManager.getInstance().updatePresence();
            }
        }, 1000, 5000);
    }

    @SubscribeEvent
    public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        t.cancel();
        t = new Timer();

        RPCManager.getInstance().menuPresence();
    }
}
