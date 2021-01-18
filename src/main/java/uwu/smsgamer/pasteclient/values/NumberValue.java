package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import uwu.smsgamer.pasteclient.command.CommandException;
import uwu.smsgamer.pasteclient.gui.clickgui.utils.Utils;
import uwu.smsgamer.pasteclient.utils.MathUtil;

import java.util.Locale;
import java.util.function.Function;

public class NumberValue extends Value<Double> {
    protected Double min;
    protected Double max;
    protected Double step;
    protected NumberType type;

    public NumberValue(String name, String description,
                          Number dVal, Number min, Number max, Number step, NumberType type, Value<?>... children) {
        super(name, description, dVal.doubleValue(), children);
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.step = step.doubleValue();
        this.type = type;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getStep() {
        return step;
    }

    public NumberType getType() {
        return type;
    }

    public int getInt() {
        return value.intValue();
    }

    @Override
    public void setValue(Object val) {
        setValue(((Number) val).doubleValue());
    }

    @Override
    public boolean setCommandValue(String arg) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new CommandException("Not a valid number: " + arg + "!");
        }
        if (type.equals(NumberValue.NumberType.INTEGER)) value = Math.floor(doubleValue);
        else value = doubleValue;
        return true;
    }

    public void setValue(Double val) {
        this.value = MathUtil.roundInc(val, step);
    }

    public double getValScaled() {
        return (value - min) / (max - min);
    }

    public void setValScaled(Double val) {
        this.value = MathUtil.roundInc(((val * (max - min)) + min), step);
    }

    @Override
    public String getValStr() {
        return type.formatter.apply(value);
    }

    @Override
    public JsonElement toElement() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromElement(JsonElement ele) {
        value = ele.getAsDouble();
    }

    public enum NumberType {//                                  24%
        PERCENT(number -> String.format(Locale.ENGLISH, "%.0f%%", number.floatValue() * 100)),
        TIME(number -> Utils.formatTime(number.longValue())),
        DECIMAL(number -> String.format(Locale.ENGLISH, "%.4f", number.floatValue())),
        INTEGER(number -> Long.toString(number.longValue()));

        final Function<Number, String> formatter;

        NumberType(Function<Number, String> formatter) {
            this.formatter = formatter;
        }

        public Function<Number, String> getFormatter() {
            return formatter;
        }
    }
}
