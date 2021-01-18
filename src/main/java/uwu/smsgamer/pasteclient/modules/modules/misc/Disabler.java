/*
package uwu.smsgamer.pasteclient.modules.modules.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import uwu.smsgamer.pasteclient.events.PacketEvent;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.*;
import uwu.smsgamer.pasteclient.values.*;

import java.lang.reflect.*;

public class Disabler extends PasteModule {
    public IntChoiceValue mode = addIntChoice("Mode", "The mode of the disabler.", 0,
      0, "PacketDetect",
      1, "Timing");
    public PacketValue detectPacket = (PacketValue) addValue(new PacketValue("DetectPacket", "The Packet that gets detected.", CPacketPlayer.class));
    public PacketValue sendPacket = (PacketValue) addValue(new PacketValue("SendPacket", "The Packet that gets detected.", CPacketPlayer.class, PacketValue.cPacketChoices));

    public PositionValue posValue = (PositionValue) addValue(new PositionValue("Position", "Position of the move packet.") {
        @Override
        public boolean isVisible() {
            return sendPacket.getValue().equals(CPacketPlayer.class);
        }
    });
    public BoolValue onGround = (BoolValue) addValue(new BoolValue("OnGround", "To set the packet on ground.", false) {
        @Override
        public boolean isVisible() {
            return sendPacket.getValue().equals(CPacketPlayer.class);
        }
    });
    public BoolValue notOnGround = (BoolValue) addValue(new BoolValue("NotOnGround", "To set the packet not on ground.", false) {
        @Override
        public boolean isVisible() {
            return sendPacket.getValue().equals(CPacketPlayer.class);
        }
    });

    // Special cases

    //CPacketAnimation
    public BoolValue offhand = (BoolValue) addValue(new BoolValue("OffHand", "If your animation is for the off hand.", false) {
        @Override
        public boolean isVisible() {
            return sendPacket.getValue().equals(CPacketAnimation.class);
        }
    });

    //CPacketClientStatus
    @SuppressWarnings("unchecked")
    public ChoiceValue<CPacketClientStatus.State> state = (ChoiceValue<CPacketClientStatus.State>) addValue(new ChoiceValue<CPacketClientStatus.State>(
      "State", "State for the ClientStatus.", CPacketClientStatus.State.PERFORM_RESPAWN,
      new StringHashMap<>(CPacketClientStatus.State.PERFORM_RESPAWN, "PERFORM_RESPAWN",
        CPacketClientStatus.State.REQUEST_STATS, "REQUEST_STATS")) {
        @Override
        public CPacketClientStatus.State getCommandT(String arg) {
            return CPacketClientStatus.State.valueOf(arg);
        }

        @Override
        public boolean isVisible() {
            return sendPacket.getValue().equals(CPacketClientStatus.class);
        }
    });

    public Disabler() {
        super("Disabler", "Attempts to disable the anticheat", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) setState(false);
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (!getState()) return;
        if (mode.getValue().equals(0) && detectPacket.getValue().isInstance(event.getPacket())) {
            try {
                Class<? extends Packet<?>> packetClass = sendPacket.getValue();
                if (packetClass.equals(CPacketAnimation.class)) {
                    mc.thePlayer.connection.sendPacket(packetClass.getDeclaredConstructor(EnumHand.class).newInstance(offhand.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                } else if (packetClass.equals(CPacketClientStatus.class)) {
                    mc.thePlayer.connection.sendPacket(packetClass.getDeclaredConstructor(CPacketClientStatus.State.class).newInstance(state.getValue()));
                } else if (packetClass.equals(CPacketPlayer.class)) {
                    CPacketPlayer packet = (CPacketPlayer) packetClass.getDeclaredConstructor(boolean.class).newInstance(
                      onGround.getValue() || (!notOnGround.getValue() && mc.thePlayer.onGround)
                    );// TODO: 2020-11-19 PosValue
                    mc.thePlayer.connection.sendPacket(packet);
                } else {
                    mc.thePlayer.connection.sendPacket(packetClass.newInstance());
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                ChatUtils.info("Cannot make new instance of that packet!");
            }
        }
    }
}
*/
