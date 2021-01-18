package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import uwu.smsgamer.pasteclient.command.CommandException;
import uwu.smsgamer.pasteclient.utils.StringHashMap;

import java.util.Collection;

public abstract class ChoiceValue<T> extends Value<T> {

    protected StringHashMap<T> choices;

    public ChoiceValue(String name, String description, T val, StringHashMap<T> choices, Value<?>... children) {
        super(name, description, val, children);
        this.choices = choices;
    }

    public StringHashMap<T> getChoices() {
        return choices;
    }

    public String getChoice(T t) {
        return choices.get(t);
    }

    @Override
    public String getValStr() {
        return choices.get(value);
    }

    public abstract T getCommandT(String arg);

    @Override
    public boolean setCommandValue(String arg) {
        T newValue = getCommandT(arg);
        if (!choices.containsKey(newValue)) return false;
        value = newValue;
        return true;
    }

    public Collection<String> getChoicesAsString(T t) {
        return choices.values();
    }

    @Override
    public JsonElement toElement() {
        return new JsonPrimitive(choices.get(value));
    }

    @Override
    public void fromElement(JsonElement ele) {
        value = choices.getReversedMap().get(ele.getAsString());
        if (value == null) value = dVal;
    }
}
