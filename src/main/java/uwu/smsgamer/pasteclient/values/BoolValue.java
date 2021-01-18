package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;

public class BoolValue extends Value<Boolean> {

    public BoolValue(String name, String description, Boolean val, Value<?>... children) {
        super(name, description, val, children);
    }

    @Override
    public boolean setCommandValue(String arg) {
        if (arg.equalsIgnoreCase("true") ||
          arg.equalsIgnoreCase("yes") ||
          arg.equalsIgnoreCase("on") ||
          arg.equalsIgnoreCase("enable") ||
          arg.equalsIgnoreCase("enabled")) {
            value = true;
        } else if (arg.equalsIgnoreCase("toggle") ||
          arg.equalsIgnoreCase("switch")) {
            value = !value;
        } else value = false;
        return true;
    }

    @Override
    public JsonElement toElement() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromElement(JsonElement ele) {
        value = ele.getAsBoolean();
    }
}
