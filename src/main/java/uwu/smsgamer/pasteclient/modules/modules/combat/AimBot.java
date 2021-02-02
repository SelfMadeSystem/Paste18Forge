package uwu.smsgamer.pasteclient.modules.modules.combat;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import uwu.smsgamer.pasteclient.events.*;
import uwu.smsgamer.pasteclient.injection.interfaces.IMixinMouseHelper;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.*;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;

public class AimBot extends PasteModule {
    public IntChoiceValue targetOrder = addIntChoice("TargetOrder      ->", "Which entities to target first.", 0,
      0, "Closest",
      1, "Lowest Health",
      2, "Least Angle");
    public IntChoiceValue aimMode = addIntChoice("AimMode", "How to aim at entities.", 0,
      0, "Normal",
      1, "MouseHelper",
      2, "GCD Patch");
    public IntChoiceValue aimWhere = addIntChoice("AimWhere", "Where to aim on the entity.", 0,
      0, "Auto",
      1, "Top",
      2, "Eyes",
      3, "Middle",
      4, "Legs",
      5, "Bottom",
      6, "Custom");
    public NumberValue customAim = (NumberValue) addValue(new NumberValue("CustomAim", "Where to aim.", 0.5, 0, 1, 0.01, NumberValue.NumberType.PERCENT) {
        @Override
        public boolean isVisible() {
            return aimWhere.getValue() == 6;
        }
    });
    public RangeValue hLimit = (RangeValue) addValue(new RangeValue("HLimit", "Horizontal limit on entity for aiming.", 1, 1, 0, 1, 0.01, NumberValue.NumberType.PERCENT));
    public NumberValue aimLimit = (NumberValue) addValue(new NumberValue("AimLimit", "Error loading description.", 90, 0, 90, 1, NumberValue.NumberType.INTEGER) {
        @Override
        public String getDescription() {
            switch (aimMode.getValue()) {
                default:
                    return "Error loading description.";
                case 0:
                case 2:
                    return "Limits your aim in degrees.";
                case 1:
                    return "Limits your aim in mouse pixels.";
            }
        }
    });
    public NumberValue aimLimitVary = (NumberValue) addValue(new NumberValue("AimLimitVary", "Error loading description.", 5, 0, 15, 0.5, NumberValue.NumberType.DECIMAL) {
        @Override
        public String getDescription() {
            switch (aimMode.getValue()) {
                default:
                    return "Error loading description.";
                case 0:
                case 2:
                    return "Varies the limit of your aim in degrees.";
                case 1:
                    return "Varies the limit of your aim in mouse pixels.";
            }
        }
    });
    public NumberValue aimRandomYaw = (NumberValue) addValue(new NumberValue("AimRandomYaw", "Error loading description.", 2, 0, 15, 0.1, NumberValue.NumberType.DECIMAL) {
        @Override
        public String getDescription() {
            switch (aimMode.getValue()) {
                default:
                    return "Error loading description.";
                case 0:
                case 2:
                    return "Randomizes your yaw in degrees.";
                case 1:
                    return "Randomizes your yaw in mouse pixels.";
            }
        }
    });
    public NumberValue aimRandomPitch = (NumberValue) addValue(new NumberValue("AimRandomPitch", "Error loading description.", 2, 0, 15, 0.1, NumberValue.NumberType.DECIMAL) {
        @Override
        public String getDescription() {
            switch (aimMode.getValue()) {
                default:
                    return "Error loading description.";
                case 0:
                case 2:
                    return "Randomizes your pitch in degrees.";
                case 1:
                    return "Randomizes your pitch in mouse pixels.";
            }
        }
    });
    public BoolValue mark = addBool("Mark", "Whether to mark where you're aiming at.", true);
    public FancyColorValue color = (FancyColorValue) addValue(new FancyColorValue("Mark Color", "Color for the marker.", new Color(0, 255, 0, 64)) {
        @Override
        public boolean isVisible() {
            return mark.getValue();
        }
    });
    public NumberValue maxRange;
    public NumberValue maxAngle;

    public AimBot() {
        super("AimBot", "Automatically aims at entities", ModuleCategory.COMBAT);
        targetOrder.addChild(maxRange = genDeci("MaxRange", "Maximum distance the entity has to be in blocks.", 6, 2, 16, 0.5));
        targetOrder.addChild(maxAngle = genInt("MaxAngle", "Maximum angle the entity has to be in degrees.", 180, 0, 180));
    }

    public Entity lastTarget;

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
        if (target != null) {
            RotationUtil util = new RotationUtil(target);
            boolean setY = aimWhere.getValue() != 0;
            double sY = getYPos();

            Rotation rotation = util.getClosestRotation(setY, sY, hLimit.getRandomValue());
            if (aimMode.getValue() != 1) {
                rotation = RotationUtil.limitAngleChange(Rotation.player(), rotation, aimLimit);
                Rotation r = RotationUtil.rotationDiff(rotation, Rotation.player());
                boolean moving = r.yaw != 0 || r.pitch != 0;
                rotation = new Rotation(rotation.yaw + (!moving ? 0 : aimRandomYaw.getValue() * Math.random() - aimRandomYaw.getValue() / 2),
                  rotation.pitch + (!moving ? 0 : aimRandomPitch.getValue() * Math.random() - aimRandomPitch.getValue() / 2));
            }
            switch (aimMode.getValue()) {
                case 1:
                    IMixinMouseHelper mh = (IMixinMouseHelper) mc.mouseHelper;
                    mh.setMode(2);
                    rotation = RotationUtil.rotationDiff(rotation, Rotation.player());
                    boolean moving = rotation.yawToMouse() != 0 || rotation.pitchToMouse() != 0;
                    mh.setAddX((int) (Math.min(aimLimit, Math.max(-aimLimit, rotation.yawToMouse())) +
                      (!moving ? 0 : (aimRandomYaw.getValue() * 2 * Math.random() - aimRandomYaw.getValue()))));
                    mh.setAddY((int) (Math.min(aimLimit,
                      Math.max(-aimLimit, rotation.pitchToMouse())) * (mc.gameSettings.invertMouse ? 1 : -1) +
                      (!moving ? 0 : (aimRandomPitch.getValue() * 2 * Math.random() - aimRandomPitch.getValue()))));
                    break;
                case 2:
                    double f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    double gcd = f * f * f * 1.2F;
                    rotation = new Rotation(rotation.yaw - rotation.yaw % gcd, rotation.pitch - rotation.pitch % gcd);
                case 0:
                    rotation.toPlayer();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + aimMode.getValue());
            }
        } else ((IMixinMouseHelper) mc.mouseHelper).reset();
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
                return customAim.getValue();
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
