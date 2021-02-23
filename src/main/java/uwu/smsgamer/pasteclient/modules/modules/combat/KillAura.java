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
      2, "To MouseHelper Pixels",
      3, "Emulate MouseHelper",
      4, "AimBot"); // Todo: Meow
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
    public RangeValue hLimit = (RangeValue) addValue(new RangeValue("HLimit", "Horizontal limit on entity for aiming.", 1, 1, 0, 3, 0.01, NumberValue.NumberType.PERCENT));
    public RangeValue vLimit = (RangeValue) addValue(new RangeValue("VLimit", "Vertical limit on entity for aiming.", 1, 1, 0, 3, 0.01, NumberValue.NumberType.PERCENT));
    public NumberValue aimLimit = (NumberValue) addValue(new NumberValue("AimLimit", "Limits your aim in degrees.", 90, 0, 90, 1, NumberValue.NumberType.INTEGER));
    public NumberValue aimLimitVary = (NumberValue) addValue(new NumberValue("AimLimitVary", "Varies the limit of your aim in degrees.", 5, 0, 15, 0.5, NumberValue.NumberType.DECIMAL));
    public NumberValue aimRandomYaw = (NumberValue) addValue(new NumberValue("AimRandomYaw", "Randomizes your yaw in degrees.", 2, 0, 15, 0.1, NumberValue.NumberType.DECIMAL));
    public NumberValue aimRandomPitch = (NumberValue) addValue(new NumberValue("AimRandomPitch", "Error loading description.", 2, 0, 15, 0.1, NumberValue.NumberType.DECIMAL));
    public BoolValue silent = addBool("Silent", "If the KillAura rotations are silent.", true);
    public IntChoiceValue rotationStrafe = (IntChoiceValue) addValue(new IntChoiceValue("RotationStrafe", "How to aim at entities.", 0,
      new StringHashMap<>(0, "Off",
        1, "Strict",
        2, "Dynamic")) {
        @Override
        public boolean isVisible() {
            return silent.getValue();
        }
    });
    public BoolValue mark = addBool("Mark", "Whether to mark where you're aiming at.", true);
    public BoolValue justHit = addBool("JustHit", "Just hit the entity and don't fuck around w/ raycast bs.", false);
    public BoolValue dontHitBlock = addBool("DontHitBlock", "Don't hit block (can flag antishits).", false);
    public FancyColorValue color = (FancyColorValue) addValue(new FancyColorValue("Mark Color", "Color for the marker.", new Color(0, 255, 0, 64)) {
        @Override
        public boolean isVisible() {
            return mark.getValue();
        }
    });
    public NumberValue maxRange;
    public NumberValue maxAngle;
    public RangeValue hitTickDelay = (RangeValue) addValue(new RangeValue("HitTickDelay", "Delay for hitting in ticks.", 1, 3, 1, 20, 1, NumberValue.NumberType.INTEGER));
    public RangeValue hitDelayBias = (RangeValue) addValue(new RangeValue("HitDelayBias", "Bias for the tick delay.", 0, 0.3, 0, 1, 0.001, NumberValue.NumberType.PERCENT));
    public RangeValue hitDelayInfluence = (RangeValue) addValue(new RangeValue("HitDelayInfl", "Influence for the bias of the tick delay.", 0.5, 1, 0, 1, 0.001, NumberValue.NumberType.PERCENT));
    public NumberValue reach = (NumberValue) addValue(new NumberValue("Reach", "Reach for hitting entity.", 3, 0, 8, 0.025, NumberValue.NumberType.DECIMAL));

    public KillAura() {
        super("KillAura", "Automatically attacks stuff around you.", ModuleCategory.COMBAT);
        targetOrder.addChild(maxRange = genDeci("MaxRange", "Maximum distance the entity has to be in blocks.", 6, 2, 16, 0.5));
        targetOrder.addChild(maxAngle = genInt("MaxAngle", "Maximum angle the entity has to be in degrees.", 180, 0, 180));
    }

    public CustomMouseHelper mh = new CustomMouseHelper();
    public Entity lastTarget;
    public Rotation prevRotation;
    public double angleDiff;
    public Rotation startAngle;
    public int switchCount;

    @Override
    protected void onEnable() {
        if (mc.thePlayer != null) {
            mh.reinitialize();
            mh.prevYaw = mc.thePlayer.prevRotationYaw;
            mh.prevPitch = mc.thePlayer.prevRotationPitch;
            mh.yaw = mc.thePlayer.rotationYaw;
            mh.pitch = mc.thePlayer.rotationPitch;
            startAngle = Rotation.player();
            prevRotation = Rotation.player();
        }
    }

    @Override
    protected void onDisable() {
        ((IMixinMouseHelper) mc.mouseHelper).reset();
        lastTarget = null;
    }

    @EventTarget
    private void onRender(Render3DEvent event) {
        if (!getState()) return;
        if (mark.getValue() && lastTarget != null) render(lastTarget);
    }

    @EventTarget
    private void onMouseMove(MouseMoveEvent event) {
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
        CASE: if (target != null) {
            if (aimMode.getValue() == 4) {
                AIMBOTMOUSEMOVETHINGYFUCKMYLIFE(event);
                break CASE;
            }
            mh.mult = 1;
            RotationUtil util = new RotationUtil(target, mh.yaw, mh.pitch);
            boolean setY = aimWhere.getValue() != 0;
            double sY = getYPos();

            Rotation rotation = util.getRotationInfo(setY, sY, hLimit.getRandomValue(), vLimit.getRandomValue()).closestRotation;
            if (aimMode.getValue() != 3) {
                rotation = RotationUtil.limitAngleChange(mh.toRotation(), rotation, aimLimit);
                Rotation r = RotationUtil.rotationDiff(rotation, new Rotation(mh.yaw, mh.pitch));
                boolean moving = r.yaw != 0 || r.pitch != 0;
                rotation = new Rotation(rotation.yaw + (!moving ? 0 : aimRandomYaw.getValue() * Math.random() - aimRandomYaw.getValue() / 2),
                  rotation.pitch + (!moving ? 0 : aimRandomPitch.getValue() * Math.random() - aimRandomPitch.getValue() / 2));
            }
            switch (aimMode.getValue()) {
                case 1:
                    double f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcd = f * f * f * 1.2F;
                    rotation = new Rotation(rotation.yaw - rotation.yaw % gcd, rotation.pitch - rotation.pitch % gcd);
                case 0:
                    mh.prevYaw = mh.yaw;
                    mh.prevPitch = mh.pitch;
                    mh.yaw += RotationUtil.angleDiff(rotation.yaw.floatValue(), mh.yaw);
                    mh.pitch = rotation.pitch.floatValue();
                    break;
                case 2:
                    int amtX = MathUtil.toMouse(RotationUtil.angleDiff(rotation.yaw.floatValue(), mh.yaw));
                    int amtY = MathUtil.toMouse(RotationUtil.angleDiff(rotation.pitch.floatValue(), mh.pitch));
                    double aX = MathUtil.toDeg(amtX);
                    double aY = MathUtil.toDeg(amtY);
                    mh.yaw += aX;
                    mh.pitch += aY;
                    break;
                case 3:
                    rotation = RotationUtil.rotationDiff(rotation, mh.toRotation());
                    boolean moving = rotation.yawToMouse() != 0 || rotation.pitchToMouse() != 0;
                    mh.deltaX = (int) (Math.min(aimLimit, Math.max(-aimLimit, rotation.yawToMouse())) +
                      (!moving ? 0 : (aimRandomYaw.getValue() * 2 * Math.random() - aimRandomYaw.getValue())));
                    mh.deltaY = (int) (Math.min(aimLimit,
                      Math.max(-aimLimit, rotation.pitchToMouse())) * (mc.gameSettings.invertMouse ? 1 : -1) +
                      (!moving ? 0 : (aimRandomPitch.getValue() * 2 * Math.random() - aimRandomPitch.getValue())));
                    mh.rotate();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + aimMode.getValue());
            }
            if (!silent.getValue()) mh.toPlayer();
//            attack();
        } else {
            mh.reinitialize();
            mh.prevYaw = mh.yaw;
            mh.prevPitch = mh.pitch;
            mh.yaw = mc.thePlayer.rotationYaw;
            mh.pitch = mc.thePlayer.rotationPitch;
        }
        angleDiff = prevRotation.getDiffS(mh.toRotation());
        if (angleDiff > 0.00000001) prevRotation = mh.toRotation();
    }

    private void AIMBOTMOUSEMOVETHINGYFUCKMYLIFE(MouseMoveEvent event) {
        mh.mult = 0.15D;
        if (!getState()) return;
        Entity target = lastTarget;
        double aimLimit = this.aimLimit.getValue() + (Math.random() * aimLimitVary.getValue() - aimLimitVary.getValue() / 2);
        lastTarget = target;

        RotationUtil util = new RotationUtil(target);
        boolean setY = aimWhere.getValue() != 0;
        double sY = getYPos();

        Rotation rotation = util.getRotationInfo(setY, sY, hLimit.getRandomValue(), vLimit.getRandomValue()).closestRotation;
        rotation = RotationUtil.rotationDiff(rotation, Rotation.player());
        boolean moving = rotation.yawToMouse() != 0 || rotation.pitchToMouse() != 0;
        mh.deltaX = ((int) (Math.min(aimLimit, Math.max(-aimLimit, rotation.yawToMouse())) +
          (!moving ? 0 : (aimRandomYaw.getValue() * 2 * Math.random() - aimRandomYaw.getValue()))));
        mh.deltaY = ((int) (Math.min(aimLimit,
          Math.max(-aimLimit, rotation.pitchToMouse())) * (mc.gameSettings.invertMouse ? 1 : -1) +
          (!moving ? 0 : (aimRandomPitch.getValue() * 2 * Math.random() - aimRandomPitch.getValue()))));
        mh.rotate();
        if (!silent.getValue()) mh.toPlayer();
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (!getState()) return;
        if (!event.getEventType().equals(EventType.SEND)) return;
        if (!silent.getValue()) return;
        Packet<?> packet = event.getPacket();
        if (packet.getClass().equals(C03PacketPlayer.C05PacketPlayerLook.class) ||
          packet.getClass().equals(C03PacketPlayer.class)) {
            if (angleDiff > 0.00000001) {
                event.setPacket(new C03PacketPlayer.C05PacketPlayerLook(
                  prevRotation.yaw.floatValue(), prevRotation.pitch.floatValue(),
                  ((C03PacketPlayer) packet).isOnGround()));
            } else {
                event.setPacket(new C03PacketPlayer(((C03PacketPlayer) packet).isOnGround()));
            }
        } else if (packet.getClass().equals(C03PacketPlayer.C06PacketPlayerPosLook.class) ||
          packet.getClass().equals(C03PacketPlayer.C04PacketPlayerPosition.class)) {
            if (angleDiff > 0.00000001) {
                event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                  ((C03PacketPlayer) packet).getPositionX(),
                  ((C03PacketPlayer) packet).getPositionY(),
                  ((C03PacketPlayer) packet).getPositionZ(),
                  prevRotation.yaw.floatValue(), prevRotation.pitch.floatValue(),
                  ((C03PacketPlayer) packet).isOnGround()));
            } else {
                event.setPacket(new C03PacketPlayer.C04PacketPlayerPosition(
                  ((C03PacketPlayer) packet).getPositionX(),
                  ((C03PacketPlayer) packet).getPositionY(),
                  ((C03PacketPlayer) packet).getPositionZ(),
                  ((C03PacketPlayer) packet).isOnGround()));
            }
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (!getState()) return;
        if (event.getEventType().equals(EventType.PRE)) attack();
    }

    @EventTarget
    private void onStrafe(StrafeEvent event) { // thx LB
        if (!getState()) return;
        if (lastTarget != null && silent.getValue()) {
            switch (rotationStrafe.getValue()) {
                case 1: {
                    double strafe = event.strafe;
                    double forward = event.forward;
                    double friction = event.friction;

                    double f = strafe * strafe + forward * forward;

                    if (f >= 1.0E-4F) {
                        f = Math.sqrt(f);

                        if (f < 1.0F)
                            f = 1.0F;

                        f = friction / f;
                        strafe *= f;
                        forward *= f;

                        double yawSin = MathUtil.sin(mh.yaw);
                        double yawCos = MathUtil.cos(mh.yaw);

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin;
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin;
                    }
                    event.setCancelled(true);
                    break;
                }
                case 2: {
                    mh.toRotation().applyStrafeToPlayer(event);
                    event.setCancelled(true);
                }
            }
        }
    }

    private int nextAttack;

    public void attack() {
        if (nextAttack-- <= 0 && lastTarget != null) {
            if (justHit.getValue()) {
                if (targetMode.getValue() == 2) switchCount++;
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, lastTarget);
            } else {
                float rY = mc.thePlayer.rotationYaw;
                float rP = mc.thePlayer.rotationPitch;
                float pRY = mc.thePlayer.prevRotationYaw;
                float pRP = mc.thePlayer.prevRotationPitch;

                mc.thePlayer.rotationYaw = mh.yaw;
                mc.thePlayer.rotationPitch = mh.pitch;
                mc.thePlayer.prevRotationYaw = mh.prevYaw;
                mc.thePlayer.prevRotationPitch = mh.prevPitch;
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
            nextAttack = (int) hitTickDelay.getRandomBias(hitDelayBias.getRandomValue(), hitDelayInfluence.getRandomValue());
            // ChatUtils.send(String.format("%s %s", mc.thePlayer.rotationYaw, mc.thePlayer.cameraYaw));
        }
    }

    private void clickMouse(MovingObjectPosition objectMouseOver) {
        mc.thePlayer.swingItem();
        if (objectMouseOver == null) {
            LogManager.getLogger().error("Null returned as 'hitResult', this shouldn't happen!");
        } else {
            switch (objectMouseOver.typeOfHit) {
                case ENTITY:
                    if (targetMode.getValue() == 2) switchCount++;
                    mc.playerController.attackEntity(mc.thePlayer, objectMouseOver.entityHit);
                    break;
                case BLOCK:
                    if (dontHitBlock.getValue()) return;
                    BlockPos blockpos = objectMouseOver.getBlockPos();
                    if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                        mc.playerController.clickBlock(blockpos, objectMouseOver.sideHit);
                        break;
                    }
            }
        }
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
        double h = hLimit.getRandomValue();
        double v = vLimit.getRandomValue() / 2;
        aabb = new AxisAlignedBB(entity.posX - lenX * h,
          entity.posY + lenY * (setY ? getYPos() : 0.5 - v),
          entity.posZ - lenZ * h,
          entity.posX + lenX * h,
          entity.posY + lenY * (setY ? getYPos() : 0.5 + v),
          entity.posZ + lenZ * h);
        GLUtil.drawAxisAlignedBBRel(aabb, color.getColor());
    }
}
