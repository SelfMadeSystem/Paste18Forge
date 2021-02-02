package uwu.smsgamer.pasteclient.modules.modules.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import org.apache.logging.log4j.LogManager;
import uwu.smsgamer.pasteclient.events.*;
import uwu.smsgamer.pasteclient.injection.interfaces.IMixinMouseHelper;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.*;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

public class KillAura extends PasteModule {

    public IntChoiceValue targetOrder = addIntChoice("TargetOrder      ->", "Which entities to target first.", 0,
      0, "Closest",
      1, "Lowest Health",
      2, "Least Angle");
    public IntChoiceValue targetMode = addIntChoice("TargetMode", "How to target entities.", 0,
      0, "Single",
      1, "Constant",
      2, "Switch");
    public IntChoiceValue aimMode = addIntChoice("AimMode", "How to aim at entities.", 0,
      0, "Normal",
      1, "GCD Patch",
      2, "Emulate MouseHelper");
    public IntChoiceValue aimWhere = addIntChoice("AimWhere", "Where to aim on the entity.", 0,
      0, "Auto",
      1, "Top",
      2, "Eyes",
      3, "Middle",
      4, "Legs",
      5, "Bottom",
      6, "Custom");
    public RangeValue customAim = (RangeValue) addValue(new RangeValue("CustomAim", "Where to aim.", 0.5, 0.5, 0, 1, 0.01, NumberValue.NumberType.PERCENT) {
        @Override
        public boolean isVisible() {
            return aimWhere.getValue() == 6;
        }
    });
    public NumberValue customAimTime = (NumberValue) addValue(new NumberValue("CustomAimTime", "Where to aim.", 80, 1, 200, 5, NumberValue.NumberType.INTEGER) {
        @Override
        public boolean isVisible() {
            return aimWhere.getValue() == 6;
        }
    });
    public RangeValue hLimit = (RangeValue) addValue(new RangeValue("HLimit", "Horizontal limit on entity for aiming.", 1, 1, 0, 1, 0.01, NumberValue.NumberType.PERCENT));
    public NumberValue aimLimit = (NumberValue) addValue(new NumberValue("AimLimit", "Limits your aim in degrees.", 90, 0, 90, 1, NumberValue.NumberType.INTEGER));
    public NumberValue aimLimitVary = (NumberValue) addValue(new NumberValue("AimLimitVary", "Varies the limit of your aim in degrees.", 5, 0, 15, 0.5, NumberValue.NumberType.DECIMAL));
    public NumberValue aimRandomYaw = (NumberValue) addValue(new NumberValue("AimRandomYaw", "Randomizes your yaw in degrees.", 2, 0, 15, 0.1, NumberValue.NumberType.DECIMAL));
    public NumberValue aimRandomPitch = (NumberValue) addValue(new NumberValue("AimRandomPitch", "Error loading description.", 2, 0, 15, 0.1, NumberValue.NumberType.DECIMAL));
    public BoolValue silent = addBool("Silent", "If the KillAura rotations are silent.", true);
    public BoolValue mark = addBool("Mark", "Whether to mark where you're aiming at.", true);
    public BoolValue justHit = addBool("JustHit", "Just hit the entity and don't fuck around w/ raycast bs.", false);
    public FancyColorValue color = (FancyColorValue) addValue(new FancyColorValue("Mark Color", "Color for the marker.", new Color(0, 255, 0, 64)) {
        @Override
        public boolean isVisible() {
            return mark.getValue();
        }
    });
    public NumberValue maxRange;
    public NumberValue maxAngle;
    public NumberValue minCPS = (NumberValue) addValue(new NumberValue("MinCPS", "Minimum CPS.", 9, 0, 20, 1, NumberValue.NumberType.INTEGER));
    public NumberValue maxCPS = (NumberValue) addValue(new NumberValue("MaxCPS", "Maximum CPS.", 13, 0, 20, 1, NumberValue.NumberType.INTEGER));
    public NumberValue reach = (NumberValue) addValue(new NumberValue("Reach", "Reach for hitting entity.", 3, 0, 8, 0.025, NumberValue.NumberType.DECIMAL));

    public KillAura() {
        super("KillAura", "Automatically attacks stuff around you.", ModuleCategory.COMBAT);
        targetOrder.addChild(maxRange = genDeci("MaxRange", "Maximum distance the entity has to be in blocks.", 6, 2, 16, 0.5));
        targetOrder.addChild(maxAngle = genInt("MaxAngle", "Maximum angle the entity has to be in degrees.", 180, 0, 180));
    }

    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    public Entity lastTarget;
    public Rotation startAngle;
    public int switchCount;

    @Override
    protected void onEnable() {
        if (mc.thePlayer != null) {
            prevYaw = mc.thePlayer.prevRotationYaw;
            prevPitch = mc.thePlayer.prevRotationPitch;
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
            startAngle = Rotation.player();
        }
    }

    @EventTarget
    private void onRender(Render3DEvent event) {
        if (!getState()) return;
        Entity target = null;
        double range = this.maxRange.getValue();
        double angle = this.maxAngle.getValue();
        double aimLimit = this.aimLimit.getValue() + (Math.random() * aimLimitVary.getValue() - aimLimitVary.getValue() / 2);

        if (targetMode.getValue() == 2) {
            List<Entity> entities = TargetUtil.getEntities(range, angle);

            if (entities.size() > 0)

                switch (targetOrder.getValue()) {
                    case 0:
                        TargetUtil.sortEntitiesByDistance(entities);
                        break;
                    case 1:
                        TargetUtil.sortEntitiesByHealth(entities);
                        break;
                    case 2:
                        TargetUtil.sortEntitiesByAngle(entities, startAngle);
                        break;
                }

            if (switchCount >= entities.size()) switchCount = 0;
            if (entities.size() > 0) target = lastTarget = entities.get(switchCount);
        } else if (targetMode.getValue() == 1 || !TargetUtil.isInRange(lastTarget, range, angle)) {
            switch (targetOrder.getValue()) {
                case 0:
                    target = TargetUtil.getClosestEntity(range, angle);
                    break;
                case 1:
                    target = TargetUtil.getLowestHealthEntity(range, angle);
                    break;
                case 2:
                    target = TargetUtil.getLowestAngleEntity(range, angle);
                    break;
            }
            lastTarget = target;
        } else target = lastTarget;
        if (target != null) {
            if (mark.getValue()) render(target);
            RotationUtil util = new RotationUtil(target, yaw, pitch);
            boolean setY = aimWhere.getValue() != 0;
            double sY = getYPos();

            Rotation rotation = util.getClosestRotation(setY, sY, hLimit.getRandomValue());
            rotation = RotationUtil.limitAngleChange(new Rotation(yaw, pitch), rotation, aimLimit);
            Rotation r = RotationUtil.rotationDiff(rotation, new Rotation(yaw, pitch));
            boolean moving = r.yaw != 0 || r.pitch != 0;
            rotation = new Rotation(rotation.yaw + (!moving ? 0 : aimRandomYaw.getValue() * Math.random() - aimRandomYaw.getValue() / 2),
              rotation.pitch + (!moving ? 0 : aimRandomPitch.getValue() * Math.random() - aimRandomPitch.getValue() / 2));
            switch (aimMode.getValue()) {
                case 1:
                    double f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcd = f * f * f * 1.2F;
                    rotation = new Rotation(rotation.yaw - rotation.yaw % gcd, rotation.pitch - rotation.pitch % gcd);
                case 0:
                    prevYaw = yaw;
                    prevPitch = pitch;
                    yaw += RotationUtil.angleDiff(rotation.yaw.floatValue(), yaw);
                    pitch = rotation.pitch.floatValue();
                    break;
                case 2:
                    int amtX = MathUtil.toMouse(RotationUtil.angleDiff(rotation.yaw.floatValue(), yaw));
                    int amtY = MathUtil.toMouse(RotationUtil.angleDiff(rotation.pitch.floatValue(), pitch));
                    double aX = MathUtil.toDeg(amtX);
                    double aY = MathUtil.toDeg(amtY);
                    yaw += aX;
                    pitch += aY;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + aimMode.getValue());
            }
            if (!silent.getValue()) rotation.toPlayer();
//            attack();
        } else {
            prevYaw = yaw;
            prevPitch = pitch;
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (!getState()) return;
        Packet<?> packet = event.getPacket();
        if (packet.getClass().equals(C03PacketPlayer.C05PacketPlayerLook.class)) {
            event.setPacket(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, ((C03PacketPlayer.C05PacketPlayerLook) packet).isOnGround()));
        } else if (packet.getClass().equals(C03PacketPlayer.C06PacketPlayerPosLook.class)) {
            event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((C03PacketPlayer.C06PacketPlayerPosLook) packet).getPositionX(),
              ((C03PacketPlayer.C06PacketPlayerPosLook) packet).getPositionY(),
              ((C03PacketPlayer.C06PacketPlayerPosLook) packet).getPositionZ(),
              yaw, pitch, ((C03PacketPlayer.C06PacketPlayerPosLook) packet).isOnGround()));
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (!getState()) return;
        if (event.getEventType().equals(EventType.PRE)) attack();
    }

    private int nextAttack;

    public void attack() {
        if (nextAttack-- <= 0 && lastTarget != null) {
            if (justHit.getValue()) {
                if (targetMode.getValue() == 2) switchCount++;
                mc.playerController.attackEntity(mc.thePlayer, lastTarget);
                mc.thePlayer.swingItem();
            } else {
                float rY = mc.thePlayer.rotationYaw;
                float rP = mc.thePlayer.rotationPitch;
                float pRY = mc.thePlayer.prevRotationYaw;
                float pRP = mc.thePlayer.prevRotationPitch;
                mc.thePlayer.rotationYaw = yaw;
                mc.thePlayer.rotationPitch = pitch;
                mc.thePlayer.prevRotationYaw = prevYaw;
                mc.thePlayer.prevRotationPitch = prevPitch;
                MovingObjectPosition result = RaycastUtils.getObjectMouseOver(1, mc.thePlayer, reach.getValue(), 6);
                clickMouse(result);
                mc.thePlayer.rotationYaw = rY;
                mc.thePlayer.rotationPitch = rP;
                mc.thePlayer.prevRotationYaw = pRY;
                mc.thePlayer.prevRotationPitch = pRP;
                /*if (result.entityHit != null) {
                    if (targetMode.getValue() == 2) switchCount++;
                    mc.playerController.attackEntity(mc.thePlayer, result.entityHit);
                } else if (result.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
                    mc.playerController.clickBlock(result.getBlockPos(), result.sideHit);
                }*/
            }
            double min = minCPS.getValue();
            double max = maxCPS.getValue();
            nextAttack = (int) (Math.random() * (max - min) + min);
        }
    }

    private void clickMouse(MovingObjectPosition objectMouseOver) {
        Field lcc = null;
        try {
            lcc = mc.getClass().getDeclaredField("leftClickCounter");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        int leftClickCounter = 0;
        if (lcc != null) {
            try {
                lcc.setAccessible(true);
                leftClickCounter = (int) lcc.get(mc);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (leftClickCounter <= 0) {
            mc.thePlayer.swingItem();
            if (objectMouseOver == null) {
                LogManager.getLogger().error("Null returned as 'hitResult', this shouldn't happen!");
                if (mc.playerController.isNotCreative()) {
                    leftClickCounter = 10;
                }
            } else {
                switch (objectMouseOver.typeOfHit) {
                    case ENTITY:
                        mc.playerController.attackEntity(mc.thePlayer, objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = objectMouseOver.getBlockPos();
                        if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            mc.playerController.clickBlock(blockpos, objectMouseOver.sideHit);
                            break;
                        }
                    case MISS:
                    default:
                        if (mc.playerController.isNotCreative()) {
                            leftClickCounter = 10;
                        }
                }
            }
        }
        if (lcc != null) {
            try {
                lcc.set(mc, leftClickCounter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDisable() {
        ((IMixinMouseHelper) mc.mouseHelper).reset();
    }

    public double getYPos() {
        switch (aimWhere.getValue()) {
            case 1:
                return 1;
            case 2:
                return 0.85;
            case 3:
                return 0.5;
            case 4:
                return 0.15;
            case 5:
            default:
                return 0;
            case 6:
                return customAim.getTimedValue(customAimTime.getInt() * 50);
        }
    }

    private void render(Entity entity) {
        AxisAlignedBB aabb = entity.getEntityBoundingBox();
        double lenX = (aabb.maxX - aabb.minX) / 2;
        double lenY = (aabb.maxY - aabb.minY);
        double lenZ = (aabb.maxZ - aabb.minZ) / 2;
        boolean setY = aimWhere.getValue() != 0;
        aabb = new AxisAlignedBB(entity.posX - lenX * hLimit.getRandomValue(), entity.posY + lenY * (setY ? getYPos() : 0), entity.posZ - lenZ * hLimit.getRandomValue(),
          entity.posX + lenX * hLimit.getRandomValue(), entity.posY + lenY * (setY ? getYPos() : 1), entity.posZ + lenZ * hLimit.getRandomValue());
        GLUtil.drawAxisAlignedBBRel(aabb, color.getColor());
    }
}
