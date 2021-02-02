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

    public void applyStrafeToPlayer() { // If it's not obvious, this is from LB xD
        /*val player = mc.thePlayer!!

          val dif = ((WMathHelper.wrapAngleTo180_float(player.rotationYaw - this.yaw
          - 23.5f - 135)
          + 180) / 45).toInt()

        val yaw = this.yaw

        val strafe = event.strafe
        val forward = event.forward
        val friction = event.friction

        var calcForward = 0f
        var calcStrafe = 0f

        when (dif) {
            0 -> {
                calcForward = forward
                calcStrafe = strafe
            }
            1 -> {
                calcForward += forward
                calcStrafe -= forward
                calcForward += strafe
                calcStrafe += strafe
            }
            2 -> {
                calcForward = strafe
                calcStrafe = -forward
            }
            3 -> {
                calcForward -= forward
                calcStrafe -= forward
                calcForward += strafe
                calcStrafe -= strafe
            }
            4 -> {
                calcForward = -forward
                calcStrafe = -strafe
            }
            5 -> {
                calcForward -= forward
                calcStrafe += forward
                calcForward -= strafe
                calcStrafe -= strafe
            }
            6 -> {
                calcForward = -strafe
                calcStrafe = forward
            }
            7 -> {
                calcForward += forward
                calcStrafe += forward
                calcForward -= strafe
                calcStrafe += strafe
            }
        }

        if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
            calcForward *= 0.5f
        }

        if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
            calcStrafe *= 0.5f
        }

        var d = calcStrafe * calcStrafe + calcForward * calcForward

        if (d >= 1.0E-4f) {
            d = sqrt(d)
            if (d < 1.0f) d = 1.0f
            d = friction / d
            calcStrafe *= d
            calcForward *= d
            val yawSin = sin((yaw * Math.PI / 180f).toFloat())
            val yawCos = cos((yaw * Math.PI / 180f).toFloat())
            player.motionX += calcStrafe * yawCos - calcForward * yawSin.toDouble()
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin.toDouble()
        }*/
    }
}
