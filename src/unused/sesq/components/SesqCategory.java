package uwu.smsgamer.pasteclient.gui.clickgui.sesq.components;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.*;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens.*;
import uwu.smsgamer.pasteclient.modules.ModuleCategory;

import java.awt.*;

public class SesqCategory extends SesqComponent {
    public final ModuleCategory category;

    public SesqCategory(SesqScreen screen, ModuleCategory category) {
        super(screen);
        this.category = category;
    }

    @Override
    public void render(double x, double y, int mouseX, int mouseY, long deltaTime) {
        setLast(x, y);
        if (isHovering(mouseX, mouseY))
            renderer.drawRoundedRect(x, y, getWidth(), getHeight(), 5, Color.LIGHT_GRAY);
        int strWidth = renderer.getStringWidth(category.getName());
        int strHeight = renderer.getStringHeight(category.getName());
        renderer.drawString((int) (x + getWidth() / 2 - strWidth / 2), (int) (y + getHeight() / 2 - strHeight / 2), category.getName(), Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (isHovering(mouseX, mouseY) && pressType == 0 && mouseButton == 0) {
            gui.setCurrentScreen(new ModuleScreen(this.screen, category));
        }
    }

    @Override
    public double getWidth() {
        return CategoryScreen.CAT_WIDTH;
    }

    @Override
    public double getHeight() {
        return CategoryScreen.CAT_HEIGHT;
    }
}
