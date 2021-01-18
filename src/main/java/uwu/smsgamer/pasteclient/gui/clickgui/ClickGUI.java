package uwu.smsgamer.pasteclient.gui.clickgui;

import net.minecraft.client.gui.GuiScreen;
import uwu.smsgamer.pasteclient.gui.clickgui.block.BlockClickGUI;
import uwu.smsgamer.pasteclient.notifications.*;

public class ClickGUI {
    public final BlockClickGUI blockClickGUI = BlockClickGUI.getInstance();
    public int selected = 0;

    public GuiScreen getSelectedGUI() {
        return blockClickGUI;
    }
}
