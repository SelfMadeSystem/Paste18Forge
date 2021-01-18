package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import uwu.smsgamer.pasteclient.utils.Colour;

import java.awt.*;
import java.lang.reflect.Field;

public class ColorValue extends Value<Color> {
    private Colour.RGB rgb;
    private Colour.HSV hsv;
    private double alpha;
    public ColorValue(String name, String description, Color dVal) {
        super(name, description, null);
        setColor(dVal);
    }

    public ColorValue(String name, String description, Color dVal, Value<?>... children) {
        super(name, description, null, children);
        setColor(dVal);
    }

    public Color getColor() {
        return rgb.toColor();
    }

    public Colour.RGB getRGB() {
        return rgb;
    }

    public Colour.HSV getHSV() {
        return hsv;
    }

    public double getAlpha() {
        return alpha;
    }

    @Override
    public Color getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.setColor(new Color(((Number) value).intValue()));
        } else if (value instanceof Color) {
            this.setColor((Color) value);
        } else if (value instanceof Colour.RGB) {
            this.setRGB((Colour.RGB) value);
        } else if (value instanceof Colour.HSV) {
            this.setHSV((Colour.HSV) value);
        } else {
            throw new IllegalArgumentException("Value is not acceptable: " + value.toString());
        }
    }

    @Override
    public boolean setCommandValue(String arg) {
        boolean swH = arg.startsWith("#");
        if (swH) {
            arg = arg.substring(1);
        }

        if (arg.length() == 6) {
            try {
                setColor(hex2Rgb(arg));
            } catch (NumberFormatException ignored) {
            }
        }

        if (swH) return false; // We don't want "#Yellow" lol
        try {
            char c = arg.charAt(0);
            c = Character.toLowerCase(c);
            Field field = Color.class.getField(c + arg.substring(1));
            setColor((Color) field.get(null));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Color hex2Rgb(String colorStr) {
        return new Color(
          Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
          Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
          Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public void setColor(Color color) {
        this.rgb = new Colour.RGB(color);
        this.hsv = Colour.rgb2hsv(this.rgb);
        this.alpha = color.getAlpha()/255F;
    }

    public void setRGB(Colour.RGB rgb) {
        this.rgb = rgb;
        this.hsv = Colour.rgb2hsv(rgb);
    }

    public void setHSV(Colour.HSV hsv) {
        this.hsv = hsv;
        this.rgb = Colour.hsv2rgb(hsv);
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public JsonElement toElement() {
        JsonObject o = new JsonObject();
        JsonObject object = new JsonObject();
        object.add("r", new JsonPrimitive(this.rgb.r));
        object.add("g", new JsonPrimitive(this.rgb.g));
        object.add("b", new JsonPrimitive(this.rgb.b));
        object.add("a", new JsonPrimitive(this.alpha));
        // Wow. I can't believe that this formatting is perfect.
        o.add("__value__", object);
        return o;
    }

    @Override
    public void fromElement(JsonElement ele) {
        JsonObject object = ele.getAsJsonObject();
        if (object.has("__value__")) object = object.get("__value__").getAsJsonObject(); // sloppy but works
        setRGB(new Colour.RGB(object.get("r").getAsDouble(), object.get("g").getAsDouble(), object.get("b").getAsDouble()));
        setAlpha(object.get("a").getAsDouble());
    }
}
