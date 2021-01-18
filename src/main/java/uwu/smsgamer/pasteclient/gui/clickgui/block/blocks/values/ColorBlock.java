package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis.ValueGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.ValueBlock;
import uwu.smsgamer.pasteclient.gui.selectors.ColorSelectorGUI;
import uwu.smsgamer.pasteclient.utils.ChatUtils;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;

public class ColorBlock extends ValueBlock {
    ColorValue value;

    public ColorBlock(BlockGUI gui, ColorValue value) {
        super(gui, value);
        this.value = value;
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        Color color = value.getColor();
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth() / 2D, getHeight(), color);
        // Fully opaque
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F + getWidth() / 2D, y - getHeight() / 2F,
          getWidth() / 2D, getHeight(), new Color(color.getRed(), color.getGreen(), color.getBlue()));
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, value.getName(),
          (((0.2126 * color.getRed()) + (0.7152 * color.getGreen()) +
            (0.0722 * color.getBlue())) < 127) ? Color.WHITE : Color.BLACK);
        setDescription(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
        if (mouseButton == 0 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            if (value instanceof FancyColorValue) gui.setChild(new ValueGUI(gui, this.value));
            else mc.displayGuiScreen(new ColorSelectorGUI(BlockClickGUI.getInstance(), value));
        }
    }
}
