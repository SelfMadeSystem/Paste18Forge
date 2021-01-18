package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis.ModuleGUI;
import uwu.smsgamer.pasteclient.modules.ModuleCategory;

import java.awt.*;

public class CategoryBlock extends BlockComponent {
    public final ModuleCategory category;

    public CategoryBlock(BlockGUI gui, ModuleCategory category) {
        super(gui);
        this.category = category;
    }


    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), canSelect() && isHovering(mouseX, mouseY) ? BlockClickGUI.CATEGORY_HOVER.getColor() : BlockClickGUI.CATEGORY.getColor());
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, category.getName(), Color.BLACK);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        // not middle
        if (mouseButton <= 1 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            gui.setChild(new ModuleGUI(gui, category));
        }
    }
}
