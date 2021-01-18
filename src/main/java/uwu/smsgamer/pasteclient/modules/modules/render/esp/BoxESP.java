package uwu.smsgamer.pasteclient.modules.modules.render.esp;

import net.minecraft.entity.Entity;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.utils.*;

import java.awt.*;

public class BoxESP extends ESPModule {
    private static BoxESP instance;
    public static BoxESP getInstance() {
      if (instance == null) instance = new BoxESP();
      return instance;
    }
    @Override
    public void onRender(Render3DEvent event, Color color) {
        for (Entity e : mc.theWorld.loadedEntityList) if (TargetUtil.isValid(e)) GLUtil.drawAxisAlignedBBRel(e.getEntityBoundingBox(), color);
    }
}
