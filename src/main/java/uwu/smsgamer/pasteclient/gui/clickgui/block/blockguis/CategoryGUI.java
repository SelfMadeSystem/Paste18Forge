package uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.*;
import uwu.smsgamer.pasteclient.modules.ModuleCategory;

public class CategoryGUI extends BlockGUI {
    public CategoryGUI() {
        for (ModuleCategory category : ModuleCategory.values()) {
            components.add(new CategoryBlock(this, category));
        }
        components.add(new BackBlock(this));
    }

    @Override
    public void reload() {
        this.components.clear();
        for (ModuleCategory category : ModuleCategory.values()) {
            components.add(new CategoryBlock(this, category));
        }
        components.add(new BackBlock(this));
    }

    @Override
    public void close() {
        BlockClickGUI.getInstance().close();
    }
}
