package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis.ChoiceGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.ValueBlock;
import uwu.smsgamer.pasteclient.values.ChoiceValue;

import java.awt.*;

public class ChoiceBlock extends ValueBlock {
    ChoiceValue<?> value;

    public ChoiceBlock(BlockGUI gui, ChoiceValue<?> value) {
        super(gui, value);
        this.value = value;
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), isHovering(mouseX, mouseY) && canSelect() ? BlockClickGUI.CHOICE_HOVER.getColor() : BlockClickGUI.CHOICE.getColor());
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, value.getName(), Color.BLACK);
        setDescription(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
        if (mouseButton == 0 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            gui.setChild(new ChoiceGUI(this.gui, this.value));
        }
    }
}
