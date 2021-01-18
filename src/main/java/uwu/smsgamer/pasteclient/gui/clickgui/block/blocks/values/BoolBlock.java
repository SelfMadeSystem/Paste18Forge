package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.ValueBlock;
import uwu.smsgamer.pasteclient.values.BoolValue;

import java.awt.*;

public class BoolBlock extends ValueBlock {
    BoolValue value;

    public BoolBlock(BlockGUI gui, BoolValue value) {
        super(gui, value);
        this.value = value;
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), value.getValue() ? Color.GREEN : Color.RED);
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, value.getName(), Color.BLACK);
        setDescription(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
        if (mouseButton == 0 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            value.setValue(!value.getValue());
            gui.reload();
        }
    }
}
