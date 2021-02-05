package uwu.smsgamer.pasteclient.injection.mixins.client;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.*;
import org.spongepowered.asm.mixin.*;
import uwu.smsgamer.pasteclient.events.EntityHitEvent;

@Mixin(PlayerControllerMP.class)
@SideOnly(Side.CLIENT)
public class MixinPlayerControllerMP {
    @Shadow
    private void syncCurrentPlayItem() {
    }

    @Final
    @Shadow
    private NetHandlerPlayClient netClientHandler;
    @Shadow
    private WorldSettings.GameType currentGameType;

    /**
     * @author Sms_Gamer_3808
     */
    @Overwrite
    public void attackEntity(EntityPlayer p, Entity e) {
        EntityHitEvent event = new EntityHitEvent(e, EventType.PRE);
        EventManager.call(event);
        if (event.isCancelled()) return;
        e = event.target;

        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(e, net.minecraft.network.play.client.C02PacketUseEntity.Action.ATTACK));
        if (this.currentGameType != WorldSettings.GameType.SPECTATOR) {
            p.attackTargetEntityWithCurrentItem(e);
        }

        EventManager.call(new EntityHitEvent(e, EventType.POST));
    }
}
