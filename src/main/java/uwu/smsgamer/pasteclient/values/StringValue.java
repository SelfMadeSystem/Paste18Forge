package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;

public class StringValue extends Value<String> {

    public StringValue(String name, String description, String val, Value<?>... children) {
        super(name, description, val, children);
    }

    @Override
    public boolean setCommandValue(String arg) {
        value = arg;
        return true;
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
