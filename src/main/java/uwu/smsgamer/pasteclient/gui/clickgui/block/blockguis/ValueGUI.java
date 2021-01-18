package uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis;

import uwu.smsgamer.pasteclient.gui.clickgui.block.BlockGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.BackBlock;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values.*;
import uwu.smsgamer.pasteclient.modules.PasteModule;
import uwu.smsgamer.pasteclient.values.*;

import java.util.Collection;

public class ValueGUI extends BlockGUI {
    public final PasteModule module;
    public final Value<?> val;

    public ValueGUI(BlockGUI parent, PasteModule module) {
        super(parent);
        this.module = module;
        this.val = null;
        setup(module.getValues().values());
    }

    public ValueGUI(BlockGUI parent, Value<?> val) {
        super(parent);
        this.module = null;
        this.val = val;
        setup(val.getChildren().values());
    }

    private void setup(Collection<Value<?>> collection) {
        for (Value<?> value : collection) {
            if (value.isVisible()) {
                if (value instanceof BoolValue) {
                    components.add(new BoolBlock(this, (BoolValue) value));
                } else if (value instanceof NumberValue) {
                    components.add(new NumBlock(this, (NumberValue) value));
                } else if (value instanceof RangeValue) {
                    components.add(new RangeBlock(this, (RangeValue) value));
                } else if (value instanceof ChoiceValue) {
                    components.add(new ChoiceBlock(this, (ChoiceValue<?>) value));
                } else if (value instanceof StringValue) {
                    components.add(new StringBlock(this, (StringValue) value));
                } else if (value instanceof ColorValue) {
                    components.add(new ColorBlock(this, (ColorValue) value));
                } else {
                    components.add(new MiscBlock(this, value));
                }
            }
        }
        if (this.val != null && this.val.allowChildAdd())
            components.add(new ChildAddBlock(this, this.val));
        components.add(new BackBlock(this));
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
    }

    @Override
    public void reload() {
        this.components.clear();
        this.setup(module == null ? val.getChildren().values() : module.getValues().values());
    }
}
