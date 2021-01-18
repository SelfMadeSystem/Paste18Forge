package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.ValueBlock;
import uwu.smsgamer.pasteclient.values.Value;

import java.awt.*;

public class ChildAddBlock extends ValueBlock {
    public ChildAddBlock(BlockGUI gui, Value<?> value) {
        super(gui, value);
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), canSelect() && isHovering(mouseX, mouseY) ? BlockClickGUI.ADD_HOVER.getColor() : BlockClickGUI.ADD.getColor());
        int width = BlockClickGUI.renderer.getStringWidth("+");
        BlockClickGUI.renderer.drawString(x - width / 2, y - getHeight() / 2, "+", Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (mouseButton == 0 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            value.genNewChild();
            gui.reload();
        }
    }
}
