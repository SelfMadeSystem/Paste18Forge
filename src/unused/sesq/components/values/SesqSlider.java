package uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.values;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.SesqScreen;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.SesqValue;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens.ValuesScreen;
import uwu.smsgamer.pasteclient.values.NumberValue;

import java.awt.*;
import java.util.function.Function;

public class SesqSlider extends SesqValue {
    public static double SLIDER_HEIGHT = 5;
    public final NumberValue value;
    public boolean selected = false;

    public SesqSlider(SesqScreen screen, NumberValue value) {
        super(screen, value);
        this.value = value;
    }

    @Override
    public void render(double x, double y, int mouseX, int mouseY, long deltaTime) {
        setLast(x, y);
        renderer.drawRect(x, y + getHeight() - SLIDER_HEIGHT, getWidth(), SLIDER_HEIGHT, selected || hoveringSlider(mouseX, mouseY) ? Color.GRAY : Color.LIGHT_GRAY);
        renderer.drawRect(x, y + getHeight() - SLIDER_HEIGHT, getWidth() * value.getValScaled(), SLIDER_HEIGHT, selected || hoveringSlider(mouseX, mouseY) ? Color.GRAY.darker() : Color.LIGHT_GRAY.darker());

        int sH = renderer.getStringHeight(value.getName());
        int sW = renderer.getStringWidth(value.getName());
        if (hasSubs())
            renderer.drawRect((int) x + 1, (int) (y + getHeight() - SLIDER_HEIGHT - sH - 2), sW + 3, sH + 1,
              hoveringText(mouseX, mouseY) ? Color.GRAY : Color.LIGHT_GRAY); //eh it works xd
        renderer.drawString((int) (x + 1), (int) (y + getHeight() - SLIDER_HEIGHT - sH - 2), value.getName(), Color.BLACK);

        Function<Number, String> format = value.getType().getFormatter();

        String s = value.getType().equals(NumberValue.NumberType.PERCENT) ? value.getValStr() : format.apply(value.getValue()) + "/" + format.apply(value.getMax());

        sH = renderer.getStringHeight(s);
        sW = renderer.getStringWidth(s);

        renderer.drawString((int) (x + getWidth() - 1 - sW), (int) (y + getHeight() - SLIDER_HEIGHT - sH - 2), value.getName(), Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (mouseButton == 0 && pressType == 0) {
            if (hoveringSlider(mouseX, mouseY)) {
                selected = true;
            } else if (hoveringText(mouseX, mouseY) && hasSubs()) {
                gui.setCurrentScreen(new ValuesScreen(screen, value));
            }
        } else if (selected && pressType == 2) {
            selected = false;
        }

        if (selected) {
            value.setValScaled(Math.max(0, Math.min(1, (mouseX * 2 - lastX) / getWidth())));
        }
    }

    @Override
    public double getWidth() {
        return 300;
    }

    @Override
    public double getHeight() {
        return 30;
    }

    public boolean hoveringText(int mouseX, int mouseY) {
        int sW = renderer.getStringWidth(value.getName());
        int sH = renderer.getStringHeight(value.getName());
        double minX = lastX;
        double maxX = lastX + sW + 2;
        double minY = lastY + getHeight() - SLIDER_HEIGHT - sH - 2;
        double maxY = minY + sH + 1;
        mouseX *= 2;
        mouseY *= 2;
        return minX < mouseX && maxX > mouseX && minY < mouseY && maxY > mouseY;
    }

    public boolean hoveringSlider(int mouseX, int mouseY) {
        double minX = lastX;
        double maxX = lastX + getWidth();
        double minY = lastY + getHeight() - SLIDER_HEIGHT;
        double maxY = lastY + getHeight();
        mouseX *= 2;
        mouseY *= 2;
        return minX < mouseX && maxX > mouseX && minY < mouseY && maxY > mouseY;
    }
}
