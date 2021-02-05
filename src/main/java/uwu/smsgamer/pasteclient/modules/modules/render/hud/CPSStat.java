package uwu.smsgamer.pasteclient.modules.modules.render.hud;

import uwu.smsgamer.pasteclient.modules.modules.render.HUD;

public class CPSStat implements HudStat {
    @Override
    public boolean render(HUD hud) {
        return hud.eStatCPS.getValue();
    }

    @Override
    public String getStatString(HUD hud) {
        return "CPS: " + hud.cps.size();
    }
}
