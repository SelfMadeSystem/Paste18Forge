package uwu.smsgamer.pasteclient.injection.mixins.network;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uwu.smsgamer.pasteclient.events.VelocityEvent;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Shadow
    private WorldClient clientWorldController;

    /**
     * @author Sms_Gamer_3808
     */
    @Inject(method = "handleEntityVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"), cancellable = true)
    public void velocity(S12PacketEntityVelocity packet, CallbackInfo ci) {
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityID());
        ci.cancel();
        VelocityEvent event = new VelocityEvent(entity, (double) packet.getMotionX() / 8000.0D, (double) packet.getMotionY() / 8000.0D, (double) packet.getMotionZ() / 8000.0D);
        EventManager.call(event);
        if (event.isCancelled()) return;
        entity.setVelocity(event.x, event.y, event.z);
    }
}
