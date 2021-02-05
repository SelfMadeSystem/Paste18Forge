package uwu.smsgamer.pasteclient.modules.modules.render.hud;

import net.minecraft.client.Minecraft;
import uwu.smsgamer.pasteclient.modules.modules.render.HUD;

public class BPSStat implements HudStat {
    @Override
    public boolean render(HUD hud) {
        return hud.eStatBPS.getValue();
    }

    @Override
    public String getStatString(HUD hud) {
        Minecraft mc = Minecraft.getMinecraft();
        double currSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
        return String.format("BPS: %.2f", currSpeed);
    }
}
