package uwu.smsgamer.pasteclient.modules.modules.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.util.Vec3;
import uwu.smsgamer.pasteclient.events.*;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.ChatUtils;
import uwu.smsgamer.pasteclient.values.*;

public class Velocity extends PasteModule {
//    private BoolValue combatOnly = addBool("CombatOnly", "Only apply changes during combat.",true);
    private PositionValue initMult = (PositionValue) addValue(new PositionValue("InitMult",
      "Initial multiplication", false, false, false, 0));
    private IntChoiceValue mode = addIntChoice("Mode", "Mode for after init mult.", 0,
      0, "Multiply",
      1, "Add",
      2, "TpBack",
      3, "Tp");
    private NumberValue delay = addInt("Delay", "Delay from initial hit",
      5, 0, 40);
    private RangeValue repeat = addRange("Repeat", "Repeat between each mod in ticks",
      5, 5, 1, 40, 1, NumberValue.NumberType.INTEGER);
    private NumberValue maxTime = addInt("MaxTime", "Maximum number of ticks since last velocity.",
      20, 1, 40);
    private PositionValue modMult = (PositionValue) addValue(new PositionValue("MultMod",
      "Modification for multiplication", false, false, false, 0) {
        @Override
        public boolean isVisible() {
            return mode.getValue() == 0;
        }
    });
    private PositionValue modAdd = (PositionValue) addValue(new PositionValue("Add/TpMod",
      "Modification for addition/teleport", false, 0) {
        @Override
        public boolean isVisible() {
            return mode.getValue() == 1 || mode.getValue() == 3;
        }
    });

    private int lastHit = 5000;
    private int nextRepeat = -1;
    private Vec3 hitPos = null;

    public Velocity() {
        super("Velocity", "Changes velocity mechanics.", ModuleCategory.COMBAT);
    }

    @EventTarget
    private void onVelocity(VelocityEvent event) {
        if (!getState()) return;
        if (event.entity != mc.thePlayer) return;
        if (mc.thePlayer.ticksExisted < 20) return;
        event.x *= initMult.getX();
        event.y *= initMult.getY();
        event.z *= initMult.getZ();
        lastHit = 0;
        hitPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    @EventTarget
    private void onUpdate(UpdateEvent event){
        if (event.getEventType() != EventType.PRE) return;
        lastHit++;
        if (!getState()) return;
        if (lastHit > delay.getValue() && lastHit <= maxTime.getValue()) {
            if (nextRepeat == -1)
                nextRepeat = (int) repeat.getRandomValue();
            if ((lastHit - delay.getValue()) % nextRepeat == 0) {
                switch (mode.getValue()) {
                    case 0:
                        mc.thePlayer.motionX *= modMult.getX();
                        mc.thePlayer.motionY *= modMult.getY();
                        mc.thePlayer.motionZ *= modMult.getZ();
                        break;
                    case 1:
                        mc.thePlayer.motionX += modAdd.getX();
                        mc.thePlayer.motionY += modAdd.getY();
                        mc.thePlayer.motionZ += modAdd.getZ();
                        break;
                    case 2:
                        mc.thePlayer.posX = hitPos.xCoord;
                        mc.thePlayer.posY = hitPos.yCoord;
                        mc.thePlayer.posZ = hitPos.zCoord;
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        break;
                    case 3:
                        mc.thePlayer.posX += modAdd.getX();
                        mc.thePlayer.posY += modAdd.getY();
                        mc.thePlayer.posZ += modAdd.getZ();
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                }
                nextRepeat = (int) repeat.getRandomValue();
            }
        } else if (lastHit > maxTime.getValue()) {
            nextRepeat = -1;
        }
    }
}
