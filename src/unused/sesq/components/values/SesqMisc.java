package uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.values;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.SesqScreen;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.SesqValue;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;

//Placeholder for values with no associated component.
public class SesqMisc extends SesqValue {

    public SesqMisc(SesqScreen screen, Value<?> value) {
        super(screen, value);
    }

    @Override
    public void render(double x, double y, int mouseX, int mouseY, long deltaTime) {
        setLast(x, y);
        int sH = renderer.getStringHeight(value.getName());
        renderer.drawString((int) (x + 2), (int) (y - sH / 2), value.getValStr() + "  -  " + value.getName(), Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {

    }

    @Override
    public double getWidth() {
        return 20;
    }

    @Override
    public double getHeight() {
        return 20;
    }
}
