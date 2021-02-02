/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.injection.mixins.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uwu.smsgamer.pasteclient.events.PacketEvent;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {
    @Shadow
    @Final
    private static Logger logger;
    @Shadow
    @Final
    public static AttributeKey<EnumConnectionState> attrKeyConnectionState;
    @Shadow
    private Channel channel;
    @Shadow
    private INetHandler packetListener;


    /**
     * @author Sms_Gamer_3808
     */
    @Overwrite
    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet packet) {
        PacketEvent event = new PacketEvent(EventType.RECIEVE, packet);
        EventManager.call(event);
        if (event.isCancelled()) {
            return;
        }
        packet = event.getPacket();
        if (this.channel.isOpen()) {
            try {
                packet.processPacket(this.packetListener);
            } catch (ThreadQuickExitException ignored) {
            }
        }
    }

    @Shadow
    public void setConnectionState(EnumConnectionState p_setConnectionState_1_) {
    }

    /**
     * @author Sms_Gamer_3808
     */
    @Overwrite
    private void dispatchPacket(Packet<?> packet, final GenericFutureListener<? extends Future<? super Void>>[] listeners) {
        PacketEvent event = new PacketEvent(EventType.SEND, packet);
        EventManager.call(event);
        if (event.isCancelled()) return;
        packet = event.getPacket();

        final EnumConnectionState fromPacketState = EnumConnectionState.getFromPacket(packet);
        final EnumConnectionState packetAttr = this.channel.attr(attrKeyConnectionState).get();
        if (packetAttr != fromPacketState && !(packet instanceof FMLProxyPacket)) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (fromPacketState != packetAttr && !(packet instanceof FMLProxyPacket)) {
                this.setConnectionState(fromPacketState);
            }

            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);
            if (listeners != null) {
                channelfuture.addListeners(listeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            Packet<?> finalPacket = packet;
            this.channel.eventLoop().execute(() -> {
                if (fromPacketState != packetAttr && !(finalPacket instanceof FMLProxyPacket)) {
                    this.setConnectionState(fromPacketState);
                }

                ChannelFuture future = this.channel.writeAndFlush(finalPacket);
                if (listeners != null) {
                    future.addListeners(listeners);
                }

                future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }
}
