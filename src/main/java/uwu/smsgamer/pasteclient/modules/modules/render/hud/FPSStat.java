package uwu.smsgamer.pasteclient.modules.modules.render.hud;

import net.minecraft.client.Minecraft;
import uwu.smsgamer.pasteclient.modules.modules.render.HUD;

public class FPSStat implements HudStat {
    @Override
    public boolean render(HUD hud) {
        return hud.eStatFPS.getValue();
    }

    @Override
    public String getStatString(HUD hud) {
        return "FPS: " + Minecraft.getDebugFPS();
    }
}
