package uwu.smsgamer.pasteclient.modules.modules.movement;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import com.google.gson.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.jetbrains.annotations.Nullable;
import uwu.smsgamer.pasteclient.events.*;
import uwu.smsgamer.pasteclient.injection.interfaces.*;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.*;
import uwu.smsgamer.pasteclient.values.*;

import java.util.*;

public class Fly extends PasteModule {
    private final IntChoiceValue mode = addIntChoice("Mode", "Mode for fly", 0,
      0, "Vanilla",
      1, "Float",
      2, "Advanced");
    private final NumberValue speed = addDeci("Speed", "How fast you go.", 1, 0, 5, 0.01);
    private final BoolValue ground = addBool("Ground", "Sets ground client side.", false);
    private final BoolValue groundSpoof = addBool("GroundSpoof", "Spoofs your ground.", false);
    private final BoolValue groundSpoofM = addBool("GroundSpoofM", "Sets spoof ground to this.", false);
    private final AdvancedSettings advanced = (AdvancedSettings) addValue(new AdvancedSettings("Advanced settings", "idk todo: this"));

    public Fly() {
        super("Fly", "Lets you fly.", ModuleCategory.MOVEMENT);
    }

    private boolean spoofGround; // Whether to spoof ground or not
    private boolean spoofGroundM; // Spoof ground to this
    public double startY; // Spoof ground to this

    @Override
    protected void onEnable() {
        if (mc.thePlayer != null) startY = mc.thePlayer.posY;
        advanced.settings.forEach(v -> v.condition.start());
    }

    @Override
    protected void onDisable() {
        ((IMixinTimer) ((IMixinMinecraft) mc).getTimer()).setTimerSpeed(1);
    }

    @EventTarget
    private void onMove(MotionUpdateEvent event) {
        if (!this.getState()) return;
        if (ground.getValue()) mc.thePlayer.onGround = true;
        spoofGround = false;
        switch (mode.getValue()) {
            case 0: {
                setSpoofGround();
                int v = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    v++;
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    v--;
                }
                MotionUtils.vset(v * speed.getValue());
                if (MotionUtils.isMoving()) {
                    MotionUtils.strafe(speed.getValue());
                } else {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                }
                break;
            }
            case 1: {
                setSpoofGround();
                mc.thePlayer.motionY = 0;
                break;
            }
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (!this.getState()) return;
        if (event.getEventType().equals(EventType.PRE) && mode.getValue() == 2) advanced.doAction(this);
    }

    private void setSpoofGround() {
        setSpoofGround(groundSpoof.getValue(), groundSpoofM.getValue());
    }

    private void setSpoofGround(boolean spoof, boolean mode) {
        spoofGround = spoof;
        spoofGroundM = mode;
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (!this.getState()) return;
        if (event.getEventType().equals(EventType.SEND)) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
                PacketUtils.CPacketPlayerBuilder builder = new PacketUtils.CPacketPlayerBuilder(packet);
                if (spoofGround) builder.setOnGround(spoofGroundM);
                event.setPacket(builder.build());
            }
        }
    }

    private static class AdvancedSettings extends ChildGen {
        private final Set<CVoid> settings = new HashSet<>();

        public AdvancedSettings(String name, String description) {
            super(name, description);
        }

        @Override
        public void removeChild(Value<?> value) {
            super.removeChild(value);
            //noinspection SuspiciousMethodCalls
            settings.remove(value);
        }

        @Override
        public void genChildren(Value<?> parentValue) {
            CVoid v = (CVoid) parentValue;
            parentValue.addChild(v.action = new ActionSettings());
            parentValue.addChild(v.condition = new ConditionSettings());
        }

        @Override
        public Value<?> genMainChild() {
            StringBuilder sb = new StringBuilder("Event");
            for (int i = 0; i <= children.size(); i++) {
                sb.append(i);
                if (!hasChild(sb.toString())) break;
                sb.setLength(sb.length() - String.valueOf(i).length());
            }
            StringValue n = new StringValue("Name", "Set the name of this event.", sb.toString());
            CVoid val = new CVoid(sb.toString(), "");
            val.addChild(n);
            val.name = n;
            val.setParent(this);
            settings.add(val);
            return val;
        }

        @Override
        public void loadFromJSON(Map.Entry<String, JsonElement> entry) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            StringValue n = new StringValue("Name", "Set the name of this event.", obj.get("Name").getAsString());
            CVoid val = new CVoid(entry.getKey(), "");
            val.addChild(n);
            val.name = n;
            val.setParent(this);
            genChildren(val);
            settings.add(val);
            addChild(val);
            JsonObject o = new JsonObject();
            o.add(entry.getKey(), obj);
            val.fromJSON(o);
        }

        public void doAction(Fly fly) {
            for (CVoid setting : settings)
                if (setting.condition.isTickGood(fly)) setting.action.doAction(fly);
        }

        private static class CVoid extends VoidValue {
            public StringValue name;
            public ActionSettings action;
            public ConditionSettings condition;

            public CVoid(String name, String description) {
                super(name, description);
            }

            @Override
            public boolean rightClickRemove() {
                return true;
            }

            @Override
            public String getName() {
                return name.getValue();
            }
        }
    }

    private static class ActionSettings extends ChildGen {
        private final Set<Action> actions = new HashSet<>();

        public ActionSettings() {
            super("Action Settings", "Customize the actions of this event.");
        }

        @Override
        public void genChildren(Value<?> parentValue) {
            genChildren(parentValue, null);
        }

        public void genChildren(Value<?> parentValue, @Nullable JsonObject object) {
            Action a = (Action) parentValue;
            parentValue.addChild(a.packet = new BoolValue("Packet", "Send packet to position.", false));
            parentValue.addChild(a.posType = new IntChoiceValue("Position Type", "Type for setting position.", 0,
              new StringHashMap<>(
                0, "Update",
                1, "Set",
                2, "Assign"
              )) {
                @Override
                public boolean isVisible() {
                    return !a.packet.getValue();
                }
            });
            parentValue.addChild(a.spoofGround = new BoolValue("SpoofGround", "Spoofs ground.", false));
            parentValue.addChild(a.groundSpoof = new BoolValue("GroundSpoof", "Spoofs yes or no ground.", false) {
                @Override
                public boolean isVisible() {
                    return a.spoofGround.getValue();
                }
            });
            parentValue.addChild(a.toFloor = new BoolValue("ToFloor", "Teleports to floor.", false) {
                @Override
                public String getDescription() {
                    return a.packet.getValue() ? "Sends packet to floor." : "Teleports to floor.";
                }
            });
            parentValue.addChild(a.position = new PositionValue("Position", "How to set the position.", false));
            parentValue.addChild(a.timer = new NumberValue("Timer",
              "To set the timer to. Set to 0 to have no effect.",
              1, 0, 10, 0.05, NumberValue.NumberType.DECIMAL));
            parentValue.addChild(a.motionXZType = new IntChoiceValue("MotionXZType", "The type of motion to set for XZ.", 0,
              new StringHashMap<>(
                0, "Set",
                1, "Add",
                2, "Times"
              )));
            parentValue.addChild(a.motionYType = new IntChoiceValue("MotionYType", "The type of motion to set for Y.", 0,
              new StringHashMap<>(
                0, "Set",
                1, "Add",
                2, "Times"
              )));
            parentValue.addChild(a.motion = new PositionValue("Motion", "The motion to set.", false));
            if (object != null)
                parentValue.fromJSON(object);
            actions.add(a);
        }

        @Override
        public void removeChild(Value<?> value) {
            super.removeChild(value);
            //noinspection SuspiciousMethodCalls
            actions.remove(value);
        }

        @Override
        public Value<?> genMainChild() {
            StringBuilder sb = new StringBuilder("Action");
            for (int i = 0; i <= children.size(); i++) {
                sb.append(i);
                if (!hasChild(sb.toString())) break;
                sb.setLength(sb.length() - String.valueOf(i).length());
            }
            StringValue n = new StringValue("Name", "Set the name of this action.", sb.toString());
            Action val = new Action(sb.toString(), "") {
                @Override
                public boolean rightClickRemove() {
                    return true;
                }

                @Override
                public String getName() {
                    return n.getValue();
                }
            };
            val.setParent(this);
            val.addChild(n);
            return val;
        }

        @Override
        public void loadFromJSON(Map.Entry<String, JsonElement> entry) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            StringValue n = new StringValue("Name", "Set the name of this action.", obj.get("Name").getAsString());
            Action val = new Action(entry.getKey(), "") {
                @Override
                public boolean rightClickRemove() {
                    return true;
                }

                @Override
                public String getName() {
                    return n.getValue();
                }
            };
            val.setParent(this);
            val.addChild(n);
            actions.add(val);
            addChild(val);
            genChildren(val, obj);
            JsonObject o = new JsonObject();
            o.add(entry.getKey(), obj);
            val.fromJSON(o);
        }

        public void doAction(Fly fly) {
            for (Action a : actions) {
                double x = a.position.getPosX();
                double y = a.toFloor.getValue() ? MotionUtils.getFloor() : a.position.getPosY();
                double z = a.position.getPosZ();
                if (a.packet.getValue()) {
                    PacketUtils.CPacketPlayerBuilder b = new PacketUtils.CPacketPlayerBuilder(mc.thePlayer, true, false);
                    b.setX(x);
                    b.setY(y);
                    b.setZ(z);
                    if (a.spoofGround.getValue()) b.setOnGround(a.groundSpoof.getValue());
                    C03PacketPlayer packet = b.build();
                    mc.thePlayer.sendQueue.addToSendQueue(packet);
                } else {
                    switch (a.posType.getValue()) {
                        case 0:
                            mc.thePlayer.setPositionAndUpdate(x, y, z);
                            break;
                        case 1:
                            mc.thePlayer.setPosition(x, y, z);
                            break;
                        case 2:
                            mc.thePlayer.posX = x;
                            mc.thePlayer.posY = y;
                            mc.thePlayer.posZ = z;
                    }
                    if (a.spoofGround.getValue()) {
                        fly.spoofGround = true;
                        fly.spoofGroundM = a.groundSpoof.getValue();
                    }
                }
                x = a.motion.getX();
                y = a.motion.getY();
                z = a.motion.getZ();

                switch (a.motionXZType.getValue()) { //0 set 1 add 2 times
                    case 0:
                        mc.thePlayer.motionX = x;
                        mc.thePlayer.motionZ = z;
                        break;
                    case 1:
                        mc.thePlayer.motionX += x;
                        mc.thePlayer.motionZ += z;
                        break;
                    case 2:
                        mc.thePlayer.motionX *= x;
                        mc.thePlayer.motionZ *= z;
                }

                switch (a.motionYType.getValue()) {
                    case 0:
                        mc.thePlayer.motionY = y;
                        break;
                    case 1:
                        mc.thePlayer.motionY += y;
                        break;
                    case 2:
                        mc.thePlayer.motionY *= y;
                }

                if (a.timer.getValue() > 0)
                    ((IMixinTimer) ((IMixinMinecraft) mc).getTimer()).setTimerSpeed(a.timer.getValue().floatValue());
            }
        }

        private static class Action extends VoidValue {
            public BoolValue packet;
            public IntChoiceValue posType;
            public BoolValue spoofGround;
            public BoolValue groundSpoof;
            public BoolValue toFloor;
            public PositionValue position;
            public NumberValue timer;
            public IntChoiceValue motionXZType;
            public IntChoiceValue motionYType;
            public PositionValue motion;

            public Action(String name, String description) {
                super(name, description);
            }
        }
    }

    private static class ConditionSettings extends ChildGen {
        private final Set<Cond> conds = new HashSet<>();

        public ConditionSettings() {
            super("Condition Settings", "Settings for triggering of event.");
        }

        @Override
        public void removeChild(Value<?> value) {
            super.removeChild(value);
            //noinspection SuspiciousMethodCalls
            conds.remove(value);
        }

        @Override
        public void genChildren(Value<?> parentValue) {
            Cond c = (Cond) parentValue;
            parentValue.addChild(c.checkTicks = new BoolValue("Check Ticks", "Checks for ticks.", true));
            parentValue.addChild(c.tickMod = new NumberValue("Ticks Modulus", "Number to do modulus of ticks.", 1, 1, 40, 1, NumberValue.NumberType.INTEGER) {
                @Override
                public boolean isVisible() {
                    return c.checkTicks.getValue();
                }
            });
            parentValue.addChild(c.tickRem = new NumberValue("Ticks Remainder", "Number to do modulus of ticks.", 0, 0, 40, 1, NumberValue.NumberType.INTEGER) {
                @Override
                public boolean isVisible() {
                    return c.checkTicks.getValue();
                }
            });
            parentValue.addChild(c.tickDel = new NumberValue("Ticks Delay", "The amount of delay from when this module starts.", 0, 0, 40, 1, NumberValue.NumberType.INTEGER) {
                @Override
                public boolean isVisible() {
                    return c.checkTicks.getValue();
                }
            });

            parentValue.addChild(c.checkXZMotion = new BoolValue("Check XZ Motion", "Checks for horizontal motion.", false));

            parentValue.addChild(c.xzMinSpeed = new NumberValue("Min XZ Speed", "Minimum horizontal speed.", 0, 0, 4, 0.01, NumberValue.NumberType.DECIMAL) {
                @Override
                public boolean isVisible() {
                    return c.checkXZMotion.getValue();
                }
            });
            parentValue.addChild(c.xzMaxSpeed = new NumberValue("Max XZ Speed", "Maximum horizontal speed.", 0, 0, 4, 0.01, NumberValue.NumberType.DECIMAL) {
                @Override
                public boolean isVisible() {
                    return c.checkXZMotion.getValue();
                }
            });

            parentValue.addChild(c.checkYMotion = new BoolValue("Check Y Motion", "Checks for vertical motion.", false));

            parentValue.addChild(c.yMinSpeed = new NumberValue("Min Y Speed", "Minimum vertical speed.", 0, -4, 4, 0.01, NumberValue.NumberType.DECIMAL) {
                @Override
                public boolean isVisible() {
                    return c.checkYMotion.getValue();
                }
            });
            parentValue.addChild(c.yMaxSpeed = new NumberValue("Max Y Speed", "Maximum vertical speed.", 0, -4, 4, 0.01, NumberValue.NumberType.DECIMAL) {
                @Override
                public boolean isVisible() {
                    return c.checkYMotion.getValue();
                }
            });

            parentValue.addChild(c.checkBelowStartY = new BoolValue("Check Below Start Y", "Checks for posY is below starting Y.", false));

            parentValue.addChild(c.belowYOffset = new NumberValue("Below Y Offset", "Offset for checking below Y.", 0, -8, 8, 0.1, NumberValue.NumberType.DECIMAL) {
                @Override
                public boolean isVisible() {
                    return c.checkBelowStartY.getValue();
                }
            });

            parentValue.addChild(c.checkAboveStartY = new BoolValue("Check Above Start Y", "Checks for posY is above starting Y.", false));

            parentValue.addChild(c.aboveYOffset = new NumberValue("Above Y Offset", "Offset for checking above Y.", 0, -8, 8, 0.1, NumberValue.NumberType.DECIMAL) {
                @Override
                public boolean isVisible() {
                    return c.checkAboveStartY.getValue();
                }
            });

            parentValue.addChild(c.checkGround = new BoolValue("Check Ground", "Checks for being on ground.", false));
            parentValue.addChild(c.checkAir = new BoolValue("Check Air", "Checks for being in air.", false));
            conds.add(c);
        }

        public void start() {
            for (Cond c : conds)
                c.ticks = -c.tickDel.getValue().intValue();
        }

        public boolean isTickGood(Fly fly) {
            boolean result = !conds.isEmpty();
            for (Cond c : conds) {
                if (c.checkGround.getValue())
                    result = mc.thePlayer.onGround;
                if (c.checkAir.getValue())
                    result &= !mc.thePlayer.onGround;
                if (c.checkXZMotion.getValue())
                    result &= MotionUtils.getSpeed() >= c.xzMinSpeed.getValue() && MotionUtils.getSpeed() <= c.xzMaxSpeed.getValue();
                if (c.checkYMotion.getValue())
                    result &= mc.thePlayer.motionY >= c.yMinSpeed.getValue() && mc.thePlayer.motionY <= c.yMaxSpeed.getValue();
                if (c.checkTicks.getValue())
                    result &= c.ticks >= 0 && (c.ticks % c.tickMod.getValue().intValue() == c.tickRem.getValue().intValue());
                if (c.checkBelowStartY.getValue())
                    result &= (fly.startY + c.belowYOffset.getValue()) < mc.thePlayer.posY;
                if (c.checkAboveStartY.getValue())
                    result &= (fly.startY + c.aboveYOffset.getValue()) > mc.thePlayer.posY;
                ChatUtils.info(conds.size() + ":" + fly.advanced.settings.size() + ":" + c.ticks + ":" + mc.thePlayer.ticksExisted + ":" + (c.ticks % c.tickMod.getValue().intValue()));
                c.ticks++;
            }
            return result;
        }

        @Override
        public Value<?> genMainChild() {
            StringBuilder sb = new StringBuilder("Condition");
            for (int i = 0; i <= children.size(); i++) {
                sb.append(i);
                if (!hasChild(sb.toString())) break;
                sb.setLength(sb.length() - String.valueOf(i).length());
            }
            StringValue n = new StringValue("Name", "Set the name of this condition.", sb.toString());
            Cond val = new Cond(sb.toString(), "") {
                @Override
                public boolean rightClickRemove() {
                    return true;
                }

                @Override
                public String getName() {
                    return n.getValue();
                }
            };
            val.setParent(this);
            val.addChild(n);
            return val;
        }

        @Override
        public void loadFromJSON(Map.Entry<String, JsonElement> entry) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            StringValue n = new StringValue("Name", "Set the name of this condition.", obj.get("Name").getAsString());
            Cond val = new Cond(entry.getKey(), "") {
                @Override
                public boolean rightClickRemove() {
                    return true;
                }

                @Override
                public String getName() {
                    return n.getValue();
                }
            };
            val.setParent(this);
            val.addChild(n);
            conds.add(val);
            addChild(val);
            genChildren(val);
            JsonObject o = new JsonObject();
            o.add(entry.getKey(), obj);
            val.fromJSON(o);
        }

        private static class Cond extends VoidValue {
            public int ticks;
            public BoolValue checkTicks;
            public NumberValue tickMod;
            public NumberValue tickRem;
            public NumberValue tickDel;
            public BoolValue checkXZMotion;
            public NumberValue xzMinSpeed;
            public NumberValue xzMaxSpeed;
            public BoolValue checkYMotion;
            public NumberValue yMinSpeed;
            public NumberValue yMaxSpeed;
            public BoolValue checkBelowStartY;
            public NumberValue belowYOffset;
            public BoolValue checkAboveStartY;
            public NumberValue aboveYOffset;
            public BoolValue checkGround;
            public BoolValue checkAir;

            public Cond(String name, String description) {
                super(name, description);
            }
        }
    }
}
