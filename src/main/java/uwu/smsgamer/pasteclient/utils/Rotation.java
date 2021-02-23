package uwu.smsgamer.pasteclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import uwu.smsgamer.pasteclient.events.StrafeEvent;

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
        return Math.abs(RotationUtil.angleDiff(rotation.yaw, yaw)) +
          Math.abs(RotationUtil.angleDiff(rotation.pitch, pitch));
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

    public void applyStrafeToPlayer(StrafeEvent event) { // If it's not obvious, this is from LB xD
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        int dif = (int) ((MathHelper.wrapAngleTo180_double(player.rotationYaw - this.yaw - 23.5f - 135) + 180) / 45);

        float strafe = event.strafe;
        float forward = event.forward;
        float friction = event.friction;

        float calcForward = 0f;
        float calcStrafe = 0f;

        switch(dif) {
            case 0: {
                calcForward = forward;
                calcStrafe = strafe;
                break;
            }
            case 1: {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }
            case 2: {
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }
            case 3: {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }
            case 4: {
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }
            case 5: {
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }
            case 6: {
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }
            case 7: {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
            }
        }

        if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
            calcForward *= 0.5f;
        }

        if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
            calcStrafe *= 0.5f;
        }

        double d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = Math.sqrt(d);
            if (d < 1.0f) d = 1.0f;
            d = friction / d;
            calcStrafe *= d;
            calcForward *= d;
            double yawSin = MathUtil.sin(yaw);
            double yawCos = MathUtil.cos(yaw);
            player.motionX += calcStrafe * yawCos - calcForward * yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }

    @Override
    public String toString() {
        return String.format("Rotation{yaw=%.2f, pitch=%.2f}", yaw, pitch);
    }
}
