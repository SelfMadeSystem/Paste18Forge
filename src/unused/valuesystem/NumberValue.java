/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.valuesystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uwu.smsgamer.pasteclient.utils.MathUtil;

import java.util.function.Predicate;

public class NumberValue extends Value<Double> {
    private final Double min;
    private final Double max;
    private final Double step;

    public NumberValue(String name, Number defaultVal, @NotNull Number min, @NotNull Number max) {
        this(name, defaultVal, min, max, 1, null);
    }

    public NumberValue(String name, Number defaultVal, @NotNull Number min, @NotNull Number max, @NotNull Number step) {
        this(name, defaultVal, min, max, step, null);
    }

    public NumberValue(String name, Number defaultVal, @NotNull Number min, @NotNull Number max, @NotNull Number step, @Nullable Predicate<Double> validator) {
        super(name, defaultVal.doubleValue(), validator == null ? val -> val >= min.doubleValue() && val <= max.doubleValue() : validator.and(val -> val >= min.doubleValue() && val <= max.doubleValue()));
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.step = step.doubleValue();
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

    @Override
    public boolean setObject(Double object) {
        return super.setObject(MathUtil.roundInc(object, step));
    }

    public double getDouble() {
        return object;
    }

    public int getInt() {
        return object.intValue();
    }

    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {
                setObject(obj.get(getName()).getAsNumber().doubleValue());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
