/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.modules;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import uwu.smsgamer.pasteclient.notifications.*;
import uwu.smsgamer.pasteclient.utils.StringHashMap;
import uwu.smsgamer.pasteclient.values.*;

import java.util.*;

public abstract class PasteModule { //TODO: ALSO NEED TO BE REDONE
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected String name;
    protected String description;
    protected ModuleCategory category;
    protected boolean canBeEnabled;
    protected boolean hidden;
    protected int keybind;
    protected boolean state;

    protected LinkedHashMap<String, Value<?>> values = new LinkedHashMap<>();

    protected PasteModule(String name, String description, ModuleCategory moduleCategory) {
        this(name, description, moduleCategory, true, false, Keyboard.KEY_NONE);
    }

    protected PasteModule(String name, String description, ModuleCategory category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public HashMap<String, Value<?>> getValues() {
        return values;
    }

    public Value<?> getValue(String arg) {
        return values.get(arg.toLowerCase());
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if (state == this.state) return;
        if (state) {
            this.state = true;
            onEnable();
            NotificationManager.show(new Notification(NotificationType.INFO, getName(), getName() + " was enabled", 1));
        } else {
            this.state = false;
            onDisable();
            NotificationManager.show(new Notification(NotificationType.INFO, getName(), getName() + " was disabled", 1));
        }
    }

    public void toggle() {
        setState(!state);
    } //this is not skidded. It wasn't part of this for some reason lol

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public static BoolValue genBool(String name, String description, boolean dval) {
        return (new BoolValue(name, description, dval));
    }

    public static NumberValue genDeci(String name, String description, Number dVal, Number min, Number max, Number step) {
        return genNum(name, description, dVal, min, max, step, NumberValue.NumberType.DECIMAL);
    }

    public static NumberValue genInt(String name, String description, Number dVal, Number min, Number max) {
        return genNum(name, description, dVal, min, max, 1, NumberValue.NumberType.INTEGER);
    }

    public static NumberValue genPer(String name, String description, Number dVal) {
        return genNum(name, description, dVal, 0, 1, 0.01, NumberValue.NumberType.PERCENT);
    }

    public static NumberValue genNum(String name, String description, Number dVal, Number min, Number max, Number step, NumberValue.NumberType type) {
        return (new NumberValue(name, description, dVal, min, max, step, type));
    }

    public static RangeValue genRange(String name, String description, Number dMin, Number dMax, Number min, Number max, Number step, NumberValue.NumberType type) {
        return (new RangeValue(name, description, dMin, dMax, min, max, step, type));
    }

    public static StringValue genStr(String name, String description, String dVal) {
        return (new StringValue(name, description, dVal));
    }

    public static StrChoiceValue genStrChoice(String name, String description, String dVal, Object... vals) {
        return (new StrChoiceValue(name, description, dVal, new StringHashMap<>(vals)));
    }

    public static IntChoiceValue genIntChoice(String name, String description, int dVal, Object... vals) {
        return (new IntChoiceValue(name, description, dVal, new StringHashMap<>(vals)));
    }

    public static VoidValue genVoid(String name, String description) {
        return new VoidValue(name, description);
    }

    public BoolValue addBool(String name, String description, boolean dval) {
        return (BoolValue) addValue(genBool(name, description, dval));
    }

    public NumberValue addDeci(String name, String description, Number dVal, Number min, Number max, Number step) {
        return addNum(name, description, dVal, min, max, step, NumberValue.NumberType.DECIMAL);
    }

    public NumberValue addInt(String name, String description, Number dVal, Number min, Number max) {
        return addNum(name, description, dVal, min, max, 1, NumberValue.NumberType.INTEGER);
    }

    public NumberValue addPer(String name, String description, Number dVal) {
        return addNum(name, description, dVal, 0, 1, 0.01, NumberValue.NumberType.PERCENT);
    }

    public NumberValue addNum(String name, String description, Number dVal, Number min, Number max, Number step, NumberValue.NumberType type) {
        return (NumberValue) addValue(genNum(name, description, dVal, min, max, step, type));
    }

    public RangeValue addRange(String name, String description, Number dMin,Number dMax, Number min, Number max, Number step, NumberValue.NumberType type) {
        return (RangeValue) addValue(genRange(name, description, dMin, dMax, min, max, step, type));
    }

    public StringValue addStr(String name, String description, String dVal) {
        return (StringValue) addValue(genStr(name, description, dVal));
    }

    public StrChoiceValue addStrChoice(String name, String description, String dVal, Object... vals) {
        return (StrChoiceValue) addValue(genStrChoice(name, description, dVal, vals));
    }

    public IntChoiceValue addIntChoice(String name, String description, int dVal, Object... vals) {
        return (IntChoiceValue) addValue(genIntChoice(name, description, dVal, vals));
    }

    public VoidValue addVoid(String name, String description) {
        return (VoidValue) addValue(genVoid(name, description));
    }

    public Value<?> addValue(Value<?> val) {
        val.module = this;
        values.put(val.getName().toLowerCase().replace(" ", "-"), val);
        return val;
    }
}
