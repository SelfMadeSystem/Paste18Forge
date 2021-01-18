package uwu.smsgamer.pasteclient.gui.clickgui.sesq.components;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.*;
import uwu.smsgamer.pasteclient.values.Value;

public abstract class SesqValue extends SesqComponent {
    public final Value<?> value;
    protected SesqValue(SesqScreen screen, Value<?> value) {
        super(screen);
        this.value = value;
    }

    /**
     * Returns if the value has any sub values.
     * @return if the value has any sub values.
     */
    public boolean hasSubs() {
        return value.hasChildren();
    }
}
