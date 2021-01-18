package uwu.smsgamer.pasteclient.gui.clickgui.sesq.components;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.*;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens.*;
import uwu.smsgamer.pasteclient.modules.Module;
import uwu.smsgamer.pasteclient.utils.ChatUtils;

import java.awt.*;

public class SesqModule extends SesqComponent {
    public final Module module;

    public SesqModule(SesqScreen screen, Module module) {
        super(screen);
        this.module = module;
    }

    @Override
    public void render(double x, double y, int mouseX, int mouseY, long deltaTime) {
        setLast(x, y);
        if (isHovering(mouseX, mouseY))
            renderer.drawRoundedRect(x, y, getWidth(), getHeight(), 5, Color.LIGHT_GRAY);
        int strWidth = renderer.getStringWidth(module.getName());
        int strHeight = renderer.getStringHeight(module.getName());
        renderer.drawString((int) (x + getWidth() / 2D - strWidth / 2), (int) (y + getHeight() / 2 - strHeight / 2), module.getName(), Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (isHovering(mouseX, mouseY) && pressType == 0 && mouseButton == 0) {
            gui.setCurrentScreen(new ValuesScreen(this.screen, module));
        }
    }

    @Override
    public double getWidth() {
        return ModuleScreen.MOD_WIDTH;
    }

    @Override
    public double getHeight() {
        return ModuleScreen.MOD_HEIGHT;
    }
}
