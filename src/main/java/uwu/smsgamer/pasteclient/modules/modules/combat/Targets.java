package uwu.smsgamer.pasteclient.modules.modules.combat;

import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.TargetUtil;
import uwu.smsgamer.pasteclient.values.VoidValue;

public class Targets extends PasteModule {
    public Targets() {
        super("Targets", "Settings for targeting.", ModuleCategory.COMBAT);
        addValue(TargetUtil.players);
        addValue(TargetUtil.mobs);
        addValue(TargetUtil.animals);
        addValue(TargetUtil.others);
        addValue(TargetUtil.invisible);
        VoidValue antiBot = addVoid("AntiBot", "Settings to bypass bots.");
        antiBot.addChild(TargetUtil.air);
        antiBot.addChild(TargetUtil.ground);
        antiBot.addChild(TargetUtil.tab);
        antiBot.addChild(TargetUtil.tabEquals);
        antiBot.addChild(TargetUtil.tabCase);
    }

    @Override
    public void setState(boolean state) {
        // We don't want this to ever be enabled.
    }

    @Override
    public boolean getState() {
        return false;
    }
}
