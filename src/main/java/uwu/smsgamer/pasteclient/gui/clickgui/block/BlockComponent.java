package uwu.smsgamer.pasteclient.gui.clickgui.block;

import net.minecraft.client.Minecraft;
import uwu.smsgamer.pasteclient.values.NumberValue;

public abstract class BlockComponent {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    public static final NumberValue WIDTH = new NumberValue("Width", "Width for each component", 350, 100, 1000, 10, NumberValue.NumberType.INTEGER);
    public static final NumberValue HEIGHT = new NumberValue("Height", "Height for each component", 20, 5, 50, 1, NumberValue.NumberType.INTEGER);
    public final BlockGUI gui;
    public int lastX, lastY;

    protected BlockComponent(BlockGUI gui) {
        this.gui = gui;
    }

    public void render(int x, int y, int mouseX, int mouseY) {
        lastX = x;
        lastY = y;
        draw(x, y, mouseX, mouseY);
    }

    public abstract void draw(int x, int y, int mouseX, int mouseY);

    public abstract void click(int mouseX, int mouseY, int mouseButton, int pressType);

    public void keyPress(char typedChar, int keyCode, int pressType) {
    }

    public int getWidth() {
        return WIDTH.getInt();
    }

    public int getHeight() {
        return HEIGHT.getInt();
    }

    public boolean guiSelected() {
        return gui.child == null;
    }

    public boolean canSelect() {
        return guiSelected() && !alrdySelected();
    }

    public boolean alrdySelected() {
        return gui.activeComponent != null;
    }

    public boolean isSelected() {
        return gui.activeComponent == this;
    }

    public void setSelected() {
        gui.activeComponent = this;
    }

    public void setUnselected() {
        if (isSelected()) gui.activeComponent = null;
    }

    public boolean isHovering(double x, double y, double mouseX, double mouseY) {
        return (((mouseX * 2) > (x - (getWidth() / 2F))) && ((mouseX * 2) < (x + (getWidth() / 2F)))) &&
          (((mouseY * 2) > (y - (getHeight() / 2F))) && ((mouseY * 2) < (y + (getHeight() / 2F))));
    }

    public boolean isHovering(double mouseX, double mouseY) {
        return isHovering(lastX, lastY, mouseX, mouseY);
    }
}
