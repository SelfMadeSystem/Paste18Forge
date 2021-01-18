package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;

import java.util.Map;

public class MultiString extends ChildGen {
    public MultiString(String name, String description) {
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
    public void genChildren(Value<?> parentValue) {
    }

    @Override
    public Value<?> genMainChild() {
        StringValue val = new StringValue("Entry" + children.size(), "", "") {
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

    @Override
    public void loadFromJSON(Map.Entry<String, JsonElement> entry) {
        StringValue val = new StringValue(entry.getKey(), "", entry.getValue().getAsString()) {
            @Override
            public boolean rightClickRemove() {
                return true;
            }
        };
        val.parent = this;
        addChild(val);
    }

    @Override
    public JsonElement toElement() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromElement(JsonElement ele) {
    }
}
