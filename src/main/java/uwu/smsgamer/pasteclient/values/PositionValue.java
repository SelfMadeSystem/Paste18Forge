package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import uwu.smsgamer.pasteclient.utils.*;

public class PositionValue extends VoidValue {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean relativeX = false;
    private boolean relativeY = false;
    private boolean relativeZ = false;
    private boolean relativeYaw = false;

    public PositionValue(String name, String description) {
        super(name, description);
        addXYZChildren();
        addChild(new BoolValue("RelativeX", "Whether the X is relative to the player or not.", true));
        addChild(new BoolValue("RelativeY", "Whether the Y is relative to the player or not.", true));
        addChild(new BoolValue("RelativeZ", "Whether the Z is relative to the player or not.", true));
    }

    // For SET relatives
    public PositionValue(String name, String description, boolean relativeX, boolean relativeY, boolean relativeZ, int i) {
        super(name, description);
        addXYZChildren();
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
    }

    // Defaults
    public PositionValue(String name, String description, boolean relativeX, boolean relativeY, boolean relativeZ) {
        super(name, description);
        addXYZChildren();
        addChild(new BoolValue("RelativeX", "Whether the X is relative to the player or not.", relativeX));
        addChild(new BoolValue("RelativeY", "Whether the Y is relative to the player or not.", relativeY));
        addChild(new BoolValue("RelativeZ", "Whether the Z is relative to the player or not.", relativeZ));
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
    }

    // For Yaw
    public PositionValue(String name, String description, boolean relativeYaw) {
        super(name, description);
        addXYZChildren();
        BoolValue yawR = (BoolValue) addChild(new BoolValue("RelativeYaw", "Whether the position is relative to the player's yaw or not.", relativeYaw));
        addChild(new BoolValue("RelativeX", "Whether the X is relative to the player or not.", relativeX) {
            @Override
            public boolean isVisible() {
                return !yawR.getValue();
            }
        });
        addChild(new BoolValue("RelativeY", "Whether the Y is relative to the player or not.", relativeY));
        addChild(new BoolValue("RelativeZ", "Whether the Z is relative to the player or not.", relativeZ) {
            @Override
            public boolean isVisible() {
                return !yawR.getValue();
            }
        });
        this.relativeYaw = relativeYaw;
    }

    private void addXYZChildren() {
        addChild(new NumberValue("X", "Coordinates in X axis", 0, -10, 10, 0.000001, NumberValue.NumberType.DECIMAL) {
            @Override
            public String getName() {
                return isRelative("Yaw") ? "Forwards/Backwards" : "X";
            }

            @Override
            public String getDescription() {
                return isRelative("Yaw") ? "Coordinates based on yaw (forwards/backwards)" : "Coordinates in X axis";
            }
        });
        addChild(new NumberValue("XExp", "Exponent of X", 0, -308, 308, 1, NumberValue.NumberType.INTEGER) {
            @Override
            public String getName() {
                return isRelative("Yaw") ? "F/BExp" : "XExp";
            }

            @Override
            public String getDescription() {
                return isRelative("Yaw") ? "Exponent of the forwards/backwards" : "Exponent of X";
            }
        });
        addChild(new NumberValue("Y", "Coordinates in Y axis", 0, -10, 10, 0.000001, NumberValue.NumberType.DECIMAL));
        addChild(new NumberValue("YExp", "Exponent of Y", 0, -308, 308, 1, NumberValue.NumberType.INTEGER));
        addChild(new NumberValue("Z", "Coordinates in Z axis", 0, -10, 10, 0.000001, NumberValue.NumberType.DECIMAL) {
            @Override
            public String getName() {
                return isRelative("Yaw") ? "Left/Right" : "Z";
            }

            @Override
            public String getDescription() {
                return isRelative("Yaw") ? "Coordinates based on yaw (left/right)" : "Coordinates in Z axis";
            }
        });
        addChild(new NumberValue("XExp", "Exponent of X", 0, -308, 308, 1, NumberValue.NumberType.INTEGER) {
            @Override
            public String getName() {
                return isRelative("Yaw") ? "L/RExp" : "ZExp";
            }

            @Override
            public String getDescription() {
                return isRelative("Yaw") ? "Exponent of the left/right" : "Exponent of Z";
            }
        });
    }

    public boolean isRelative(String s) {
        Value<?> val = getChild("Relative" + s);
        if (val != null)
            return ((BoolValue) getChild("Relative" + s)).getValue();

        final String lower = s.toLowerCase();
        if (lower.equals("x")) return relativeX;
        if (lower.equals("y")) return relativeY;
        if (lower.equals("z")) return relativeZ;
        if (lower.equals("yaw")) return relativeYaw;
        return false;
    }

    public double getPosX() {
        if (isRelative("Yaw"))
            return getX() + mc.thePlayer.posX;
        return getX() + (isRelative("X") ? mc.thePlayer.posX : 0);
    }

    public double getPosY() {
        return getY() + (isRelative("Y") ? mc.thePlayer.posY : 0);
    }

    public double getPosZ() {
        if (isRelative("Yaw"))
            return getZ() + mc.thePlayer.posZ;
        return getZ() + (isRelative("Z") ? mc.thePlayer.posZ : 0);
    }

    public double getX() {
        double x = getPosExp('X');
        if (isRelative("Yaw"))
            return MathUtil.cos(mc.thePlayer.rotationYaw + 90) * x +
              MathUtil.cos(mc.thePlayer.rotationYaw) * -getPosExp('Z');
        return x;
    }

    public double getY() {
        return getPosExp('Y');
    }

    public double getZ() {
        double z = getPosExp('Z');
        if (isRelative("Yaw"))
            return MathUtil.sin(mc.thePlayer.rotationYaw + 90) * getPosExp('X') +
              MathUtil.sin(mc.thePlayer.rotationYaw) * -z;
        return z;
    }

    private double getPosExp(char c) {
        return ((NumberValue) getChild(String.valueOf(c))).getValue() * Math.pow(10, ((NumberValue) getChild(c + "Exp")).getValue());
    }

    @Override
    public JsonElement toElement() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromElement(JsonElement ele) {
    }
}
