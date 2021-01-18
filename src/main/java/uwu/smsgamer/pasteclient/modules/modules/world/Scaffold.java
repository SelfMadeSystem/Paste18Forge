package uwu.smsgamer.pasteclient.modules.modules.world;

import com.darkmagician6.eventapi.EventTarget;
import uwu.smsgamer.pasteclient.events.MotionUpdateEvent;
import uwu.smsgamer.pasteclient.modules.*;

public class Scaffold extends PasteModule {
    public Scaffold() {
        super("Scaffold", "Automatically places blocks in front of you.", ModuleCategory.WORLD);
    }

    @EventTarget
    private void onMove(MotionUpdateEvent event) {
        if (!this.getState()) return;
        
    }
}
