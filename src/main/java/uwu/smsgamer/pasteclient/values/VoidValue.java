package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;

public class VoidValue extends Value<Void> {
    public VoidValue(String name, String description) {
        super(name, description, null);
    }

    public VoidValue(String name, String description, Value<?>... children) {
        super(name, description, null, children);
    }

    @Override
    public JsonObject toJSON() { // Removes __value__ as it has none
        JsonObject object = new JsonObject();
        if (children.isEmpty()) {
            object.add(name, toElement());
        } else {
            JsonObject obj = new JsonObject();
            for (Value<?> child : children.values()) {
                obj.add(child.name, child.toJSON().get(child.name));
            }
            object.add(name, obj);
        }
        return object;
    }

    @Override
    public void fromJSON(JsonObject obj) { // Removes __value__ as it has none
        if (obj.has(name)) {
            if (obj.get(name).isJsonObject()) {
                obj = obj.getAsJsonObject(name);
                for (Value<?> child : children.values()) {
                    if (obj.has(child.name)) {
                        JsonObject object = new JsonObject();
                        object.add(child.name, obj.get(child.name));
                        child.fromJSON(object);
                    } else {
                        LogManager.getLogger().warn(child.name + " is not in object. Name: " + name + "  Module: " + (module == null ? "null" : module.getName()));
                    }
                }
            } else {
                fromElement(obj.get(name));
            }
        } else {
            LogManager.getLogger().warn(name + " is not in object.  Module: " + (module == null ? "null" : module.getName()));
        }
    }

    @Override
    public JsonElement toElement() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromElement(JsonElement ele) {
    }

    @Override
    public boolean setCommandValue(String arg) {
        return false; // doesn't have a value
    }
}
