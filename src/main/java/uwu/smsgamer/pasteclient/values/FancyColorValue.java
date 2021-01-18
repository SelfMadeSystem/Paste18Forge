package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import uwu.smsgamer.pasteclient.utils.*;

import java.awt.*;

public class FancyColorValue extends ColorValue implements Cloneable {
    private final IntChoiceValue mode;
    private final ColorValue main;
    private final ColorValue second;
    private final NumberValue speed;

    public FancyColorValue(String name, String description, Color color) {
        this(name, description, 0, color, color, 1000);
    }

    @Override
    public boolean setCommandValue(String arg) {
        return false; // Has no value
    }

    public FancyColorValue(String name, String description, int modeV, Color mainV, Color secondV, int speedV) {
        super(name, description, mainV);
        addChild(mode = new IntChoiceValue("Mode", "Mode for this fancy color value.", modeV,
          new StringHashMap<>(
            0, "Single Color",
            1, "Duo Color",
            2, "Rainbow")));
        addChild(main = new ColorValue("MainColor", "Main color for this value.", mainV) {
            @Override
            public void setValue(Object value) {
                if (mode.getValue() == 2) {
                    super.setValue(value);
                    super.setValue(new Colour.HSV(time() * 360, getHSV().s, getHSV().v));
                } else super.setValue(value);
            }
        });
        addChild(second = new ColorValue("SecondColor", "Secondary color for this value.", secondV) {
            @Override
            public boolean isVisible() {
                return mode.getValue() == 1;
            }
        });
        addChild(speed = new NumberValue("Speed", "Speed for the changing color in MS.", speedV, 100, 100000, 100, NumberValue.NumberType.INTEGER) {
            @Override
            public boolean isVisible() {
                return mode.getValue() > 0;
            }
        });
    }

    public double time() {
        double s = speed.getValue();
        switch (mode.getValue()) {
            case 1:
                double l = (System.currentTimeMillis() % s * 2);
                return (l > s ? -l + s * 2 : l) / s;
            case 2:
                return (System.currentTimeMillis() % s) / s;
        }
        return 0;
    }

    public double getR() {
        switch (mode.getValue()) {
            case 0:
                return main.getRGB().r;
            case 1:
                double t = time();
                return main.getRGB().r * t + second.getRGB().r * (1 - t);
            case 2:
                Colour.HSV hsv = main.getHSV();
                hsv.h = time() * 360;
                return Colour.hsv2rgb(hsv).r;
        }
        return 0;
    }

    public double getG() {
        switch (mode.getValue()) {
            case 0:
                return main.getRGB().g;
            case 1:
                double t = time();
                return main.getRGB().g * t + second.getRGB().g * (1 - t);
            case 2:
                Colour.HSV hsv = main.getHSV();
                hsv.h = time() * 360;
                return Colour.hsv2rgb(hsv).g;
        }
        return 0;
    }

    public double getB() {
        switch (mode.getValue()) {
            case 0:
                return main.getRGB().b;
            case 1:
                double t = time();
                return main.getRGB().b * t + second.getRGB().b * (1 - t);
            case 2:
                Colour.HSV hsv = main.getHSV();
                hsv.h = time() * 360;
                return Colour.hsv2rgb(hsv).b;
        }
        return 0;
    }

    public double getA() {
        switch (mode.getValue()) {
            case 0:
            case 2:
                return main.getAlpha();
            case 1:
                double t = time();
                return main.getAlpha() * t + second.getAlpha() * (1 - t);
        }
        return 0;
    }

    @Override
    public Color getColor() {
        Color c = new Color((float) getR(), (float) getG(), (float) getB(), (float) getA());
        if (mode.getValue() == 2) main.setColor(c);
        return c;
    }

    @Override
    public Colour.RGB getRGB() {
        Colour.RGB rgb = new Colour.RGB(getR(), getG(), getB());
        if (mode.getValue() == 2) main.setRGB(rgb);
        return rgb;
    }

    @Override
    public Colour.HSV getHSV() {
        Colour.HSV hsv = Colour.rgb2hsv(getRGB());
        if (mode.getValue() == 2) main.setHSV(hsv);
        return hsv;
    }

    @Override
    public double getAlpha() {
        return getA();
    }

    @Override
    public JsonElement toElement() {
        JsonObject object = new JsonObject();
        object.add("mode", mode.toElement());
        object.add("main", main.toElement());
        object.add("second", second.toElement());
        object.add("speed", speed.toElement());
        return object;
    }

    @Override
    public void fromElement(JsonElement ele) {
        JsonObject object = (JsonObject) ele;
        mode.fromElement(object.get("mode"));
        main.fromElement(object.get("main"));
        second.fromElement(object.get("second"));
        speed.fromElement(object.get("speed"));
    }

    @Override
    public FancyColorValue clone() {
        try {
            return (FancyColorValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public FancyColorValue clone(String name, String description) {
        FancyColorValue value = this.clone();
        value.name = name;
        value.description = description;
        // TODO: 2020-11-26 clone childen values 
        return value;
    }
}
