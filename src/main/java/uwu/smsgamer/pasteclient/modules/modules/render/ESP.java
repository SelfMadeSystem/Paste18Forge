package uwu.smsgamer.pasteclient.modules.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.modules.modules.render.esp.*;
import uwu.smsgamer.pasteclient.utils.StringHashMap;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;

public class ESP extends PasteModule {
    private static ESP instance;

    public static ESP getInstance() {
        if (instance == null) throw new Error("???");
        return instance;
    }

    public ChoiceValue<ESPModule> mode;
    public FancyColorValue color = (FancyColorValue) addValue(new FancyColorValue("Color", "Color for ESP.", new Color(255, 0, 0, 64)));
    public NumberValue lineWidth = addDeci("LineWidth", "Width of the line", 1, 0.01, 4, 0.01);

    public ESP() {
        super("ESP", "Outlines entities", ModuleCategory.RENDER);
        if (instance != null) throw new RuntimeException("tf");
        instance = this;
        final StringHashMap<ESPModule> map = new StringHashMap<>(BoxESP.getInstance(), "Box",
          new HitBoxESP(), "HitBox",
          new OutlineBoxESP(), "OutlineBox",
          new CylinderESP(), "Cylinder",
          new OutlinedCylinderESP(), "Cylinder Outlined",
          new DickESP(), "Dick");
        this.mode = new ChoiceValue<ESPModule>("Mode", "Mode for ESP.",
          BoxESP.getInstance(), map) {
            @Override
            public ESPModule getCommandT(String arg) {
                return map.getReversedMap().get(arg);
            }
        };
        addValue(this.mode);
    }

    @EventTarget
    private void onRender(Render3DEvent event) {
        if (!getState()) return;
        ESPModule m = mode.getValue();
        if (m != null) m.onRender(event, color.getColor());
    }
}
