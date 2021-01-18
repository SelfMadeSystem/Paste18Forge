package uwu.smsgamer.pasteclient.modules.modules.misc;

import com.google.gson.JsonElement;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;
import java.util.Map;

public class ValuesTest extends PasteModule {
    public BoolValue boolVal = addBool("BoolVal", "Boolean Value", true);
    public BoolValue boolVal1 = addBool("BoolVal1", "Boolean Value", false);
    public NumberValue deciVal = addDeci("DeciVal", "Decimal Value", 4, 0.0, 5, 0.1);
    public NumberValue intVal = addInt("IntVal", "Decimal Value", 4, 0, 5);
    public NumberValue perVal = addPer("PerVal", "Percentage Value", 0.5);
    public RangeValue rangeValInt = addRange("RangeValInt", "Range Value Integer", 2, 4, 0, 5, 1, NumberValue.NumberType.INTEGER);
    public RangeValue rangeValDeci = addRange("RangeValDeci", "Range Value Decimal", 1.2, 5.3, -2, 13, 0.05, NumberValue.NumberType.DECIMAL);
    public RangeValue rangeValPer = addRange("RangeValPer", "Range Value Percentage", 0.3, 0.5, 0, 1, 0.01, NumberValue.NumberType.PERCENT);
    public StrChoiceValue strChoiceValue = addStrChoice("StrChoice", "String Choice", "a",
      "a", "zero",
      "b", "one",
      "c", "two",
      "d", "three",
      "e", "four");
    public IntChoiceValue intChoiceValue = addIntChoice("IntChoice", "Integer Choice", 0,
      0, "zero",
      1, "one",
      2, "two",
      3, "three",
      4, "four");
    public StringValue stringValue = addStr("StrVal", "String", "Hello");
    public NumberValue hiddenValue;
    public MultiString multiString = (MultiString) addValue(new MultiString("MultiStr", "Multiple Strings UwU"));
//    public PacketValue packetValue = (PacketValue) addValue(new PacketValue("Packet", "Packet Value (:", CPacketPlayer.class));
//    public PacketValue cPacketValue = (PacketValue) addValue(new PacketValue("CPacket", "Client Packet Value (:", CPacketPlayer.class, PacketValue.cPacketChoices));
//    public PacketValue sPacketValue = (PacketValue) addValue(new PacketValue("SPacket", "Server Packet Value (:", SPacketPlayerPosLook.class, PacketValue.sPacketChoices));
    public ColorValue colorValue = (ColorValue) addValue(new ColorValue("Color", "Color Value", Color.RED));
    public FancyColorValue fancyColorValue = (FancyColorValue) addValue(new FancyColorValue("FancyColor", "Color Value", Color.BLUE));
    public PositionValue positionValue = (PositionValue) addValue(new PositionValue("Position", "Position Value (:"));
    public PositionValue positionValue1 = (PositionValue) addValue(new PositionValue("Position1", "Position Value (:", true));
    public CChildGen cChildGen = (CChildGen) addValue(new CChildGen("CChildGen", "Custom child gen"));

    public ValuesTest() {
        super("ValuesTest", "Just testing values", ModuleCategory.MISC);
        Value<?> val = boolVal.addChild(genBool("AYE", "A Bool", true));
        val = val.addChild(genBool("BBB", "A Bool", true));
        val.addChild(genBool("CCC", "A Bool", true));
        boolVal.addChild(genBool("AYE1", "A Bool1", true));
        boolVal.addChild(genBool("AYE2", "A Bool2", true));
        hiddenValue = (NumberValue) addValue(new NumberValue("HiddenVal", "A hidden value UwU", 3, 1, 6, 0.01, NumberValue.NumberType.DECIMAL) {
            @Override
            public boolean isVisible() {
                return boolVal1.getValue();
            }
        });
        intVal.addChild(genBool("Cool", "Cool", false));
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer == null) {
            setState(false);
            return;
        }
        if (boolVal.getValue())
            mc.thePlayer.setPositionAndUpdate(positionValue1.getPosX(), positionValue1.getPosY(), positionValue1.getPosZ());
        else {
            mc.thePlayer.motionX = positionValue1.getX();
            mc.thePlayer.motionY = positionValue1.getY();
            mc.thePlayer.motionZ = positionValue1.getZ();
        }
    }

    private static class CChildGen extends ChildGen {
        public CChildGen(String name, String description) {
            super(name, description);
        }

        @Override
        public void genChildren(Value<?> parentValue) {
            parentValue.addChild(new BoolValue(":)", "(:", true));
        }

        @Override
        public void loadFromJSON(Map.Entry<String, JsonElement> entry) {
            VoidValue val = new VoidValue(entry.getKey(), "") {
                @Override
                public boolean rightClickRemove() {
                    return true;
                }
            };
            val.setParent(this);
            addChild(val);
            val.addChild(new BoolValue(":)", "(:", entry.getValue().getAsJsonObject().get(":)").getAsBoolean()));
        }
    }
}
