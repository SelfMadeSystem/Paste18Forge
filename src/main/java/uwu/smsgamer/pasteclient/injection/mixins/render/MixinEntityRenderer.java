package uwu.smsgamer.pasteclient.injection.mixins.render;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.utils.RaycastUtils;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    private Minecraft mc;

    @Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.BEFORE))
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        EventManager.call(new Render3DEvent(partialTicks));
    }

    /**
     * @author Sms_Gamer_3808
     */
    @Overwrite
    public void getMouseOver(float partialTicks) {
        mc.objectMouseOver = RaycastUtils.getObjectMouseOver(partialTicks);
        /*Entity entity = this.mc.getRenderViewEntity();
        if (entity != null && this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
            Vec3d vec3d = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            double d1 = d0;
            if (this.mc.playerController.extendedReach()) {
                d1 = 6.0D;
                d0 = d1;
            } else if (d0 > 3.0D) {
                flag = true;
            }

            if (this.mc.objectMouseOver != null) {
                d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3d);
            }

            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.addVector(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
            this.pointedEntity = null;
            Vec3d vec3d3 = null;
            List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, p_apply_1_ -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith()));
            double d2 = d1;

            for (Entity value : list) {
                AxisAlignedBB axisalignedbb = value.getEntityBoundingBox().grow(value.getCollisionBorderSize());
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
                if (axisalignedbb.contains(vec3d)) {
                    if (d2 >= 0.0D) {
                        this.pointedEntity = value;
                        vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                        d2 = 0.0D;
                    }
                } else if (raytraceresult != null) {
                    double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (value.getLowestRidingEntity() == entity.getLowestRidingEntity() && !value.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                this.pointedEntity = value;
                                vec3d3 = raytraceresult.hitVec;
                            }
                        } else {
                            this.pointedEntity = value;
                            vec3d3 = raytraceresult.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (this.pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D) {
                this.pointedEntity = null;
                this.mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
            }

            if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
                this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, vec3d3);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }

            this.mc.mcProfiler.endSection();
        }*/
    }
}
