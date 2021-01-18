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

public class AimAssist extends PasteModule {
    public IntChoiceValue targetOrder = addIntChoice("TargetOrder      ->", "Which entities to target first.", 0,
      0, "Closest",
      1, "Lowest Health",
      2, "Least Angle");
    public RangeValue hLimit = (RangeValue) addValue(new RangeValue("HLimit", "Horizontal limit on entity for aiming.", 1, 1, 0, 1, 0.01, NumberValue.NumberType.PERCENT));
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

    public RangeValue yawSpeed = addRange("YawSpeed", "Speed of the yaw.", 1, 1.3, 0.5, 5, 0.01, NumberValue.NumberType.PERCENT);
    public RangeValue pitchSpeed = addRange("PitchSpeed", "Speed of the pitch.", 1, 1.3, 0.5, 5, 0.01, NumberValue.NumberType.PERCENT);

    public RangeValue yawSlow = addRange("YawSlow", "Slow of the yaw.", 0.7, 1, 0.1, 2, 0.01, NumberValue.NumberType.PERCENT);
    public RangeValue pitchSlow = addRange("PitchSlow", "Slow of the pitch.", 0.7, 1, 0.1, 2, 0.01, NumberValue.NumberType.PERCENT);

    public BoolValue mark = addBool("Mark", "Whether to mark where you're aiming at.", true);
    public FancyColorValue color = (FancyColorValue) addValue(new FancyColorValue("Mark Color", "Color for the marker.", new Color(0, 255, 0, 64)) {
        @Override
        public boolean isVisible() {
            return mark.getValue();
        }
    });

    public NumberValue maxRange;
    public NumberValue maxAngle;

    public AimAssist() {
        super("AimAssist", "Helps you aims at entities", ModuleCategory.COMBAT);
        targetOrder.addChild(maxRange = genDeci("MaxRange", "Maximum distance the entity has to be in blocks.", 6, 2, 16, 0.5));
        targetOrder.addChild(maxAngle = genInt("MaxAngle", "Maximum angle the entity has to be in degrees.", 180, 0, 180));
    }

    Entity lastTarget;

    @EventTarget
    private void onMouse(MouseMoveEvent event) {
        if (!getState()) return;
        Entity target = lastTarget = null;
        double range = this.maxRange.getValue();
        double angle = this.maxAngle.getValue();
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
        if (target != null) {
            lastTarget = target;
            //if (mark.getValue()) render(target);
            RotationUtil util = new RotationUtil(target);
            boolean setY = aimWhere.getValue() != 0;
            double sY = getYPos();

            Rotation rotation = util.getClosestRotation(setY, sY, hLimit.getRandomValue());
            Rotation angleDiff = RotationUtil.rotationDiff(rotation, Rotation.player());
            IMixinMouseHelper mh = (IMixinMouseHelper) mc.mouseHelper;
            mh.setMode(3);
            if (Math.abs(angleDiff.yaw) > 0.1)
                mh.setSideX(angleDiff.yaw > 0 != mc.gameSettings.invertMouse ? 1 : -1);
            else mh.setSideX(0);
            if (Math.abs(angleDiff.pitch) > 0.1)
                mh.setSideY(angleDiff.pitch < 0 ? 1 : -1);
            else mh.setSideY(0);
            double ym = yawSpeed.getRandomValue();
            double pm = pitchSpeed.getRandomValue();
            double yd = yawSlow.getRandomValue();
            double pd = pitchSlow.getRandomValue();
            mh.setMultX(ym);
            mh.setMultY(pm);
            mh.setDivX(1 / yd);
            mh.setDivY(1 / pd);
        } else ((IMixinMouseHelper) mc.mouseHelper).reset();
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

    @Override
    protected void onDisable() {
        ((IMixinMouseHelper) mc.mouseHelper).reset();
    }


    @EventTarget
    private void onRender(Render3DEvent event) {
        if (!mark.getValue()) return;
        render(lastTarget);
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
