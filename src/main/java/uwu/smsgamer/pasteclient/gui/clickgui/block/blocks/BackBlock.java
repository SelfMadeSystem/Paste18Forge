package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;

import java.awt.*;

public class BackBlock extends BlockComponent {
    public BackBlock(BlockGUI gui) {
        super(gui);
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), canSelect() && isHovering(mouseX, mouseY) ? BlockClickGUI.BACK_HOVER.getColor() : BlockClickGUI.BACK.getColor());
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, "Back", Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (mouseButton <= 1 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            gui.close();
        }
    }
}
