package uwu.smsgamer.pasteclient.utils;

import com.google.common.base.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;

import java.util.List;

public class RaycastUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static MovingObjectPosition getObjectMouseOver(float partialTicks) {
        if (mc.playerController == null) return mc.objectMouseOver;
        return getObjectMouseOver(partialTicks, Minecraft.getMinecraft().getRenderViewEntity(), 3, mc.playerController.getBlockReachDistance());
    }

    public static MovingObjectPosition getObjectMouseOver(float partialTicks, Entity viewEntity, double eReach, double bReach) {
        MovingObjectPosition objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, new Vec3(0, 0, 0), null, null);
        Entity pointedEntity;
        if (viewEntity != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            // mc.pointedEntity = null;
            double d0 = bReach;//mc.playerController.getBlockReachDistance();
            objectMouseOver = viewEntity.rayTrace(d0, partialTicks);
            double d1 = d0;
            Vec3 vec3 = viewEntity.getPositionEyes(partialTicks);
            boolean flag = false;
            if (mc.playerController.extendedReach()) {
                d0 = eReach * 2;
                d1 = eReach * 2;
            } else if (d0 > eReach) {
                flag = true;
            }

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = viewEntity.getLook(partialTicks);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(viewEntity, viewEntity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                public boolean apply(Entity p_apply_1_) {
                    return p_apply_1_.canBeCollidedWith();
                }
            }));
            double d2 = d1;

            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1 == viewEntity.ridingEntity && !viewEntity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > eReach) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
//                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
//                    mc.pointedEntity = pointedEntity;
//                }
            }

            mc.mcProfiler.endSection();
        }
        return objectMouseOver;
        /*MovingObjectPosition objectMouseOver = null;
        Entity pointedEntity;
        if (viewEntity != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            double d0 = bReach;
            objectMouseOver = viewEntity.rayTrace(d0, partialTicks);
            Vec3 vec3d = viewEntity.getPositionEyes(partialTicks);
            boolean flag = false;
            double d1 = d0;
            if (mc.playerController.extendedReach()) {
                d1 = eReach * 2;
                d0 = d1;
            } else if (d0 > eReach) {
                flag = true;
            }

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3d);
            }

            Vec3 vec3d1 = viewEntity.getLook(1.0F);
            Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
            pointedEntity = null;
            Vec3 vec3d3 = null;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(viewEntity, viewEntity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, p_apply_1_ -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith()));
            double d2 = d1;

            for (Entity entity : list) {
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
                MovingObjectPosition MovingObjectPosition = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                if (axisalignedbb.contains(vec3d)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity;
                        vec3d3 = MovingObjectPosition == null ? vec3d : MovingObjectPosition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (MovingObjectPosition != null) {
                    double d3 = vec3d.distanceTo(MovingObjectPosition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity.getLowestRidingEntity() == viewEntity.getLowestRidingEntity() && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity;
                                vec3d3 = MovingObjectPosition.hitVec;
                            }
                        } else {
                            pointedEntity = entity;
                            vec3d3 = MovingObjectPosition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > eReach) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec3d3, null, new BlockPos(vec3d3));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec3d3);
            }

            mc.mcProfiler.endSection();
        }
        return objectMouseOver;*/
    }
}
