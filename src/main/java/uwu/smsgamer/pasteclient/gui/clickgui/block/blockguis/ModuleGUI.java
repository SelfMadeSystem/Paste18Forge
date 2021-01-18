package uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis;

import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.gui.clickgui.block.BlockGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.*;
import uwu.smsgamer.pasteclient.modules.*;

public class ModuleGUI extends BlockGUI {
    public final ModuleCategory category;

    public ModuleGUI(BlockGUI parent, ModuleCategory category) {
        super(parent);
        this.category = category;
        for (PasteModule module : PasteClient.INSTANCE.moduleManager.getModules(category)) {
            components.add(new ModuleBlock(this, module));
        }
        components.add(new BackBlock(this));
    }

    @Override
    public void reload() {
        components.clear();
        for (PasteModule module : PasteClient.INSTANCE.moduleManager.getModules(category)) {
            components.add(new ModuleBlock(this, module));
        }
        components.add(new BackBlock(this));
    }
}
