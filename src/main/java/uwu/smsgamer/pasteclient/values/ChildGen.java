package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public abstract class ChildGen extends VoidValue {
    public ChildGen(String name, String description) {
        super(name, description);
    }

    @Override
    public JsonObject toJSON() {
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
    public void genNewChild() {
        Value<?> val = genMainChild();
        addChild(val);
        genChildren(val);
    }

    public abstract void genChildren(Value<?> parentValue);

    public Value<?> genMainChild() {
        VoidValue val = new VoidValue("Entry" + children.size(), "") {
            @Override
            public boolean rightClickRemove() {
                return true;
            }
        };
        val.parent = this;
        return val;
    }

    @Override
    public boolean allowChildAdd() {
        return true;
    }


    public abstract void loadFromJSON(Map.Entry<String, JsonElement> entry);

    @Override
    public void fromJSON(JsonObject obj) {
        if (obj.has(name)) {
            if (obj.get(name).isJsonObject()) {
                obj = obj.getAsJsonObject(name);
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    loadFromJSON(entry);
                }
            } else {
                fromElement(obj.get(name));
            }
        } else {
            LogManager.getLogger().warn(name + " is not in object.  Module: " + module.getName());
        }
    }

    @Override
    public JsonElement toElement() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromElement(JsonElement ele) {
    }
}
