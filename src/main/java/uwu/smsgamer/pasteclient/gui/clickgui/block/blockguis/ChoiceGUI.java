package uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.BackBlock;
import uwu.smsgamer.pasteclient.utils.ChatUtils;
import uwu.smsgamer.pasteclient.values.ChoiceValue;

import java.awt.*;
import java.util.Map;

public class ChoiceGUI extends BlockGUI {
    public ChoiceValue<?> value;
    public ChoiceGUI(BlockGUI parent, ChoiceValue<?> value) {
        super(parent);
        this.value = value;
        for (Map.Entry<?, String> entry : this.value.getChoices().entrySet()) {
            components.add(new Choice(this, entry.getKey(), entry.getValue(), value));
        }
        components.add(new BackBlock(this));
    }

    @Override
    public void reload() {
        this.components.clear();
        for (Map.Entry<?, String> entry : this.value.getChoices().entrySet()) {
            components.add(new Choice(this, entry.getKey(), entry.getValue(), value));
        }
        components.add(new BackBlock(this));
    }

    private static class Choice extends BlockComponent {

        public Object val;
        public String name;
        public ChoiceValue<?> value;

        private Choice(BlockGUI gui, Object val, String name, ChoiceValue<?> value) {
            super(gui);
            this.val = val;
            this.name = name;
            this.value = value;
        }

        public boolean valIsSelected() {
            return value.getValue().equals(val);
        }

        @Override
        public void draw(int x, int y, int mouseX, int mouseY) {
            BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F,
              getWidth(), getHeight(), (canSelect() && isHovering(mouseX, mouseY)) ?
                (valIsSelected() ? BlockClickGUI.CHOICE_SELECT_HOVER.getColor() : BlockClickGUI.CHOICE_UNSELECT_HOVER.getColor()) :
                (valIsSelected() ? BlockClickGUI.CHOICE_SELECT.getColor() : BlockClickGUI.CHOICE_UNSELECT.getColor()));
            BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, name, Color.BLACK);
        }

        @Override
        public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
            if (mouseButton == 0 && pressType == 0 && canSelect() && isHovering(mouseX, mouseY)) {
                value.setValue(val);
                gui.reload();
                gui.parent.reload();
            }
        }
    }
}
