package uwu.smsgamer.pasteclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Rotation {
    public final Double yaw;
    public final Double pitch;
    private Double length = -1D;
    private Double lengthSqrd = -1D;

    public Rotation(Number yaw, Number pitch) {
        this.yaw = yaw.doubleValue();
        this.pitch = pitch.doubleValue();
    }

    public static Rotation player() {
        return new Rotation(Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch);
    }

    public void toPlayer() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        float yaw = this.yaw.floatValue();
        float pitch = this.pitch.floatValue();
        player.rotationYaw += RotationUtil.angleDiff(yaw, player.rotationYaw);
        player.rotationPitch = pitch;
    }

    public double getDiffS(Rotation rotation) {
        return RotationUtil.angleDiff(rotation.yaw, yaw) + RotationUtil.angleDiff(rotation.pitch, pitch);
    }

    public double playerYawDiff() {
        return RotationUtil.angleDiff(Minecraft.getMinecraft().thePlayer.rotationYaw, yaw);
    }

    public double playerPitchDiff() {
        return RotationUtil.angleDiff(Minecraft.getMinecraft().thePlayer.rotationPitch, pitch);
    }

    public int pitchToMouse() {
        return RotationUtil.toMouse(pitch);
    }

    public int yawToMouse() {
        return RotationUtil.toMouse(yaw);
    }

    public double getLength() {
        if (lengthSqrd == -1D) lengthSqrd = Math.sqrt(getLengthSquared());
        return lengthSqrd;
    }

    public double getLengthSquared() {
        if (length == -1D)
            length = playerYawDiff() * playerYawDiff() + playerPitchDiff() * playerPitchDiff();
        return length;
    }
}
