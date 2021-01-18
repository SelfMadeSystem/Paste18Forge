package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis.*;
import uwu.smsgamer.pasteclient.modules.PasteModule;

import java.awt.*;

public class ModuleBlock extends BlockComponent {
    public final PasteModule module;

    public ModuleBlock(ModuleGUI gui, PasteModule module) {
        super(gui);
        this.module = module;
    }

    private boolean wasSet = false;

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
          getWidth(), getHeight(), canSelect() && isHovering(mouseX, mouseY) ?
            (module.getState() ? BlockClickGUI.MODULE_ON_HOVER.getColor() : BlockClickGUI.MODULE_OFF_HOVER.getColor()) :
            (module.getState() ? BlockClickGUI.MODULE_ON.getColor() : BlockClickGUI.MODULE_OFF.getColor()));
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, module.getName(), Color.BLACK);
        if (canSelect() && isHovering(mouseX, mouseY)) {
            wasSet = true;
            BlockClickGUI.getInstance().currentDescription = module.getDescription();
        } else if (wasSet) {
            BlockClickGUI.getInstance().currentDescription = "";
            wasSet = false;
        }
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
            if (mouseButton == 0) module.toggle();
            else if (mouseButton == 1) gui.setChild(new ValueGUI(gui, module));
        }
    }
}
