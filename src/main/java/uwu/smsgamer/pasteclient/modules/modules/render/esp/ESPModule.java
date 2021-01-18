package uwu.smsgamer.pasteclient.modules.modules.render.esp;

import net.minecraft.client.Minecraft;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.modules.modules.render.ESP;

import java.awt.*;

public abstract class ESPModule {
    protected final static Minecraft mc;

    static {
        mc = Minecraft.getMinecraft();
    }

    public abstract void onRender(Render3DEvent event, Color color);

    public boolean selected() {
        return ESP.getInstance().mode.getValue().equals(this);
    }
}
