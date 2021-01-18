/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.modules.modules.render;

import org.lwjgl.input.Keyboard;
import uwu.smsgamer.pasteclient.gui.clickgui.ClickGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.values.*;

public class ClickGUIModule extends PasteModule {
    public static final ClickGUI clickGui = new ClickGUI();
    public VoidValue colors = (VoidValue) addValue(new VoidValue("Colors", "Change colors for the click gui."));
    public VoidValue values = (VoidValue) addValue(new VoidValue("Values", "Change values for the click gui."));

    public ClickGUIModule() {
        super("ClickGUI", "The click gui", ModuleCategory.RENDER, true, false, Keyboard.KEY_RSHIFT);
        VoidValue v = (VoidValue) colors.addChild(new VoidValue("GUI", "Change colors for the gui."));
        v.addChild(BlockClickGUI.GUI_BACKGROUND);
        v.addChild(BlockClickGUI.GUI_COLOR);
        v.addChild(BlockClickGUI.GUI_BORDER);
        v = (VoidValue) colors.addChild(new VoidValue("Category", "Change colors for the category."));
        v.addChild(BlockClickGUI.CATEGORY);
        v.addChild(BlockClickGUI.CATEGORY_HOVER);
        v = (VoidValue) colors.addChild(new VoidValue("Module", "Change colors for the modules."));
        v.addChild(BlockClickGUI.MODULE_ON);
        v.addChild(BlockClickGUI.MODULE_ON_HOVER);
        v.addChild(BlockClickGUI.MODULE_OFF);
        v.addChild(BlockClickGUI.MODULE_OFF_HOVER);
        v = (VoidValue) colors.addChild(new VoidValue("Components", "Change colors for each component."));
        v.addChild(BlockClickGUI.DEFAULT);
        v.addChild(BlockClickGUI.DEFAULT_HOVER);
        v.addChild(BlockClickGUI.BACK);
        v.addChild(BlockClickGUI.BACK_HOVER);
        v.addChild(BlockClickGUI.CHOICE);
        v.addChild(BlockClickGUI.CHOICE_HOVER);
        v.addChild(BlockClickGUI.CHOICE_UNSELECT);
        v.addChild(BlockClickGUI.CHOICE_UNSELECT_HOVER);
        v.addChild(BlockClickGUI.CHOICE_SELECT);
        v.addChild(BlockClickGUI.CHOICE_SELECT_HOVER);
        v.addChild(BlockClickGUI.NUM_BG);
        v.addChild(BlockClickGUI.NUM_BG_HOVER);
        v.addChild(BlockClickGUI.NUM_BG_SELECT);
        v.addChild(BlockClickGUI.NUM_SLIDER);
        v.addChild(BlockClickGUI.NUM_SLIDER_HOVER);
        v.addChild(BlockClickGUI.NUM_SLIDER_SELECT);
        v.addChild(BlockClickGUI.STR);
        v.addChild(BlockClickGUI.STR_HOVER);
        v.addChild(BlockClickGUI.STR_SELECT);
        v.addChild(BlockClickGUI.ADD);
        v.addChild(BlockClickGUI.ADD_HOVER);
        v = (VoidValue) values.addChild(new VoidValue("Components", "Change the values for components."));
        v.addChild(BlockComponent.WIDTH);
        v.addChild(BlockComponent.HEIGHT);
        v = (VoidValue) values.addChild(new VoidValue("GUI", "Change the values for gui itself."));
        v.addChild(BlockGUI.WIDTH_ADD);
        v.addChild(BlockGUI.childOffset);
        v.addChild(BlockGUI.animationTime);
    }

    @Override
    protected void onEnable() {
        mc.displayGuiScreen(clickGui.getSelectedGUI());
        setState(false);
    }

    @Override
    public void setState(boolean state) {
        this.state = state;
        if (state) onEnable();
        else onDisable();
    }
}
