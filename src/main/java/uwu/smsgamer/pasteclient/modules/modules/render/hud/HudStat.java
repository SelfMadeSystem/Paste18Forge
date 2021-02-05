package uwu.smsgamer.pasteclient.modules.modules.render.hud;

import uwu.smsgamer.pasteclient.modules.modules.render.HUD;

public interface HudStat {
    boolean render(HUD hud);
    String getStatString(HUD hud);
}
