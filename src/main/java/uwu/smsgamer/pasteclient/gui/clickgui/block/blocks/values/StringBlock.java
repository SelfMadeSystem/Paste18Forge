package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values;

import org.lwjgl.input.Keyboard;
import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.ValueBlock;
import uwu.smsgamer.pasteclient.values.StringValue;

import java.awt.*;

public class StringBlock extends ValueBlock {
    public StringBlock(BlockGUI gui, StringValue value) {
        super(gui, value);
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), isSelected() ? BlockClickGUI.STR_SELECT.getColor() :
            canSelect() && isHovering(mouseX, mouseY) ? BlockClickGUI.STR_HOVER.getColor() : BlockClickGUI.STR.getColor());
        int height = BlockClickGUI.renderer.getStringHeight("");
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2 + 2, value.getName(), Color.BLACK);
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y + getHeight() / 2 - height - 2, value.getValStr(), Color.BLACK);
        setDescription(mouseX, mouseY);
    }

    @Override
    public int getHeight() {
        return super.getHeight() * 2;
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
        if (mouseButton == 0 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY))
            setSelected();
        else if (mouseButton == 0 && pressType == 0 && isSelected()) setUnselected();
    }

    @Override
    public void keyPress(char typedChar, int keyCode, int pressType) {
        if (isSelected() && pressType <= 1) {
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    String str = value.getValStr();
                    if (!str.isEmpty()) value.setValue(str.substring(0, str.length() - 1));
                    break;
                case Keyboard.KEY_RETURN:
                    setUnselected();
                    break;
                default:
                    value.setValue(value.getValStr() + typedChar);
            }
        }
    }
}
