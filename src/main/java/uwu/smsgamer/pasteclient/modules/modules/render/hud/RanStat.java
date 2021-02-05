package uwu.smsgamer.pasteclient.modules.modules.render.hud;

import uwu.smsgamer.pasteclient.modules.modules.render.HUD;

public class RanStat implements HudStat {
    @Override
    public boolean render(HUD hud) {
        return hud.eStatRan.getValue();
    }

    @Override
    public String getStatString(HUD hud) {
        return String.format("Range: %.2f", hud.lastRange);
    }
}
