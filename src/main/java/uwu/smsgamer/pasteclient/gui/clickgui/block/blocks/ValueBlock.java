package uwu.smsgamer.pasteclient.gui.clickgui.block.blocks;

import uwu.smsgamer.pasteclient.gui.clickgui.block.*;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis.ValueGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blocks.values.MiscBlock;
import uwu.smsgamer.pasteclient.values.Value;

import java.awt.*;

public abstract class ValueBlock extends BlockComponent {
    public final Value<?> value;

    public ValueBlock(BlockGUI gui, Value<?> value) {
        super(gui);
        this.value = value;
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {
        BlockClickGUI.renderer.drawRect(x - getWidth() / 2F, y - getHeight() / 2F, getWidth(), getHeight(),
          canSelect() && isHovering(mouseX, mouseY) ? BlockClickGUI.DEFAULT.getColor() : BlockClickGUI.DEFAULT_HOVER.getColor());
        BlockClickGUI.renderer.drawString(x - getWidth() / 2, y - getHeight() / 2, value.getName() +
          (this.value.hasChildren() ? "  ->" : ""), Color.BLACK);
        // TODO: 2020-11-13 if there's no more options:    Name
        //                  if there is more options:      Name           >
        //                  just make it be on the right side and don't put 17k spaces kthx.
        setDescription(mouseX, mouseY);
    }

    private boolean wasSet = false;

    public void setDescription(int mouseX, int mouseY) {
        if (canSelect() && isHovering(mouseX, mouseY)) {
            wasSet = true;
            BlockClickGUI.getInstance().currentDescription = value.getDescription();
            BlockClickGUI.getInstance().moduleName = value.getRawName().toLowerCase().replace(" ", "-");
        } else if (wasSet) {
            BlockClickGUI.getInstance().currentDescription = "";
            BlockClickGUI.getInstance().moduleName = "";
            wasSet = false;
        }
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if ((mouseButton == 1 || (this instanceof MiscBlock && mouseButton == 0))
          && pressType == 0 && canSelect() && isHovering(mouseX, mouseY))
            if (this.value.rightClickRemove() && mouseButton == 1) {
                this.value.getParent().removeChild(this.value);
                this.gui.reload();
            } else if (this.value.hasChildren()) gui.setChild(new ValueGUI(gui, this.value));
    }
}
