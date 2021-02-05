package uwu.smsgamer.pasteclient.modules.modules.render.hud;

import uwu.smsgamer.pasteclient.modules.modules.render.HUD;

public class AAStat implements HudStat {
    @Override
    public boolean render(HUD hud) {
        return hud.eStatAAc.getValue();
    }

    @Override
    public String getStatString(HUD hud) {
        return "AA: " + ((int) (hud.aimAcc * 100)) + "%";
    }
}
