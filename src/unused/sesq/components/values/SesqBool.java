package uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.values;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.SesqScreen;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.SesqValue;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens.ValuesScreen;
import uwu.smsgamer.pasteclient.values.BoolValue;

import java.awt.*;

public class SesqBool extends SesqValue {
    public final BoolValue value;

    public SesqBool(SesqScreen screen, BoolValue value) {
        super(screen, value);
        this.value = value;
    }

    @Override
    public void render(double x, double y, int mouseX, int mouseY, long deltaTime) {
        setLast(x, y);
        renderer.drawRect(x, y, getWidth(), getHeight(), isHovering(mouseX, mouseY) ? Color.GRAY : Color.LIGHT_GRAY);
        if (value.getValue())
            renderer.drawRect(x + 2, y + 2, getWidth() - 4, getHeight() - 4, isHovering(mouseX, mouseY) ? Color.DARK_GRAY : Color.GRAY);
        int sH = renderer.getStringHeight(value.getName());
        int sW = renderer.getStringWidth(value.getName());
        if (hasSubs())
            renderer.drawRect((int) (x + getWidth() + 1), (int) y, sW + 2, (int) getHeight(),
              hoveringText(mouseX, mouseY) ? Color.GRAY : Color.LIGHT_GRAY);
        renderer.drawString((int) (x + getWidth() + 2), (int) (y + getHeight() / 2 - sH / 2), value.getName(), Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (mouseButton == 0 && pressType == 0) {
            if (isHovering(mouseX, mouseY)){
                value.setValue(!value.getValue());
            } else if (hoveringText(mouseX, mouseY) && hasSubs()) {
                gui.setCurrentScreen(new ValuesScreen(screen, value));
            }
        }
    }

    @Override
    public double getWidth() {
        return 25;
    }

    @Override
    public double getHeight() {
        return 25;
    }

    public boolean hoveringText(int mouseX, int mouseY) {
        int sW = renderer.getStringWidth(value.getName());
        double minX = (lastX + getWidth() + 1);
        double maxX = (lastX + getWidth() + sW + 2);
        double minY = lastY;
        double maxY = lastY + getHeight();
        mouseX *= 2;
        mouseY *= 2;
        return minX < mouseX && maxX > mouseX && minY < mouseY && maxY > mouseY;
    }
}
