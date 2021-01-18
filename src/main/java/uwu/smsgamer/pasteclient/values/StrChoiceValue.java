package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import uwu.smsgamer.pasteclient.utils.StringHashMap;

public class StrChoiceValue extends ChoiceValue<String> {

    public StrChoiceValue(String name, String description, String val, StringHashMap<String> values, Value<?>... children) {
        super(name, description, val, values, children);
    }

    @Override
    public String getCommandT(String arg) {
        return arg;
    }

    @Override
    public JsonElement toElement() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromElement(JsonElement ele) {
        value = ele.getAsString();
    }
}
