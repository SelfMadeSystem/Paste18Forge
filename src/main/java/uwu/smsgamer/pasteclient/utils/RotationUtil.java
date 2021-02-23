package uwu.smsgamer.pasteclient.utils;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;

import java.util.Random;

public class RotationUtil {
    private final Entity target;
    private final float yaw;
    private final float pitch;

    private static Entity p() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public RotationUtil(Entity target) {
        this(target, p().rotationYaw, p().rotationPitch);
    }

    public RotationUtil(Entity target, float yaw, float pitch) {
        this.target = target;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation getClosestRotation() {
        return getClosestRotation(false, 0, 1, 1);
    }

    // TODO: AimMode: Closest, Edge, Farthest, Random, PingPong w/ speed
    public Rotation getClosestRotation(boolean setY, double sY, double hLimit, double vLimit) {
        double length = 100000;
        Rotation closestRotation = new Rotation(-1, -1);
        AxisAlignedBB aabb = target.getEntityBoundingBox();
        Vec3 pPos = p().getPositionVector();
        pPos = pPos.addVector(0, p().getEyeHeight(), 0);
        Vec3 ePos = target.getPositionVector();
        double lenX = (aabb.maxX - aabb.minX) / 2;
        double lenY = (aabb.maxY - aabb.minY);
        double lenZ = (aabb.maxZ - aabb.minZ) / 2;
        AtomicDouble minYaw = null;
        AtomicDouble minPitch = null;
        AtomicDouble maxYaw = null;
        AtomicDouble maxPitch = null;
        AtomicDouble setYaw = null;
        AtomicDouble setPitch = null;
        double pYaw = wrapDegrees(yaw);
        double pPitch = pitch;
        double hAdd = hLimit > 0 ? hLimit / 8 : 1;
        double vAdd = vLimit > 0 ? vLimit / 8 : 1;
        vLimit /= 2;
        for (double x = -hLimit; x <= hLimit; x += hAdd) { //prevents infinite loop
            for (double z = -hLimit; z <= hLimit; z += hAdd) {
                for (double y = setY ? sY : 0.5 - vLimit; y <= (setY ? sY : vLimit + 0.5); y += vAdd) {
                    Vec3 cPos = ePos.addVector(lenX * x, lenY * y, lenZ * z);
                    Rotation r = toRotation(cPos.xCoord - pPos.xCoord, pPos.yCoord - cPos.yCoord, cPos.zCoord - pPos.zCoord);
                    if (r.getLength() <= length) {
                        length = r.getLength();
                        closestRotation = r;
                    }
                    if (minYaw == null) minYaw = new AtomicDouble(r.yaw);
                    else if (minYaw.get() > r.yaw) minYaw.set(r.yaw);
                    if (maxYaw == null) maxYaw = new AtomicDouble(r.yaw);
                    else if (maxYaw.get() < r.yaw) maxYaw.set(r.yaw);
                    if (minPitch == null) minPitch = new AtomicDouble(r.pitch);
                    else if (minPitch.get() > r.pitch) minPitch.set(r.pitch);
                    if (maxPitch == null) maxPitch = new AtomicDouble(r.pitch);
                    else if (maxPitch.get() < r.pitch) maxPitch.set(r.pitch);
                    if (isBetweenAngles(minYaw.get(), maxYaw.get(), pYaw)) setYaw = new AtomicDouble(pYaw);
                    if (isBetweenAngles(minPitch.get(), maxPitch.get(), pPitch)) setPitch = new AtomicDouble(pPitch);
                    if (setYaw != null && setPitch != null) return new Rotation(setYaw.get(), setPitch.get());
                }
            }
        }
        if (setYaw == null) setYaw = new AtomicDouble(closestRotation.yaw);
        if (setPitch == null) setPitch = new AtomicDouble(closestRotation.pitch);
        return new Rotation(setYaw.get(), setPitch.get());
    }

    public RotationInfo getRotationInfo(boolean setY, double sY, double hLimit, double vLimit) {
        vLimit /= 2;
        double length = 100000;
        AxisAlignedBB aabb = target.getEntityBoundingBox();
        double hAdd = hLimit > 0 ? hLimit / 2 : 1;
        double vAdd = vLimit > 0 ? vLimit / 2 : 1;

        Vec3 pPos = p().getPositionVector();
        pPos = pPos.addVector(0, p().getEyeHeight(), 0);
        Vec3 ePos = target.getPositionVector();
        double lenX = (aabb.maxX - aabb.minX) / 2;
        double lenY = (aabb.maxY - aabb.minY);
        double lenZ = (aabb.maxZ - aabb.minZ) / 2;

        Rotation closestRotation = new Rotation(-1, -1);
        Rotation minRotation = new Rotation(1000, 1000);
        Rotation maxRotation = new Rotation(-1000, -1000);
        for (double x = -hLimit; x <= hLimit; x += hAdd) {
            for (double y = setY ? sY : 0.5 - vLimit; y <= vLimit + 0.5; y += vAdd) {
                for (double z = -hLimit; z <= hLimit; z += hAdd) {
                    Vec3 cPos = ePos.addVector(lenX * x, lenY * y, lenZ * z);
                    Rotation r = toRotation(cPos.xCoord - pPos.xCoord, pPos.yCoord - cPos.yCoord, cPos.zCoord - pPos.zCoord);
                    if (r.getLength() <= length) {
                        length = r.getLength();
                        closestRotation = r;
                    }
                    minRotation = new Rotation(Math.min(r.yaw, minRotation.yaw), Math.min(r.pitch, minRotation.pitch));
                    maxRotation = new Rotation(Math.max(r.yaw, maxRotation.yaw), Math.max(r.pitch, maxRotation.pitch));
                }
            }
        }
        double pYaw = wrapDegrees(yaw);
        double pPitch = pitch;

        if (!isBetweenAngles(minRotation.yaw, maxRotation.yaw, pYaw)) {
            pYaw = closestRotation.yaw;
        }
        if (!isBetweenAngles(minRotation.pitch, maxRotation.pitch, pPitch)) {
            pPitch = closestRotation.pitch;
        }
        return new RotationInfo(new Rotation(wrapDegrees(yaw), pitch), new Rotation(pYaw, pPitch), minRotation, maxRotation);
    }

    public Rotation getRotation() {
        return toRotation(target.posX - p().posX, p().posY - target.posY + p().getEyeHeight(), target.posZ - p().posZ);
    }

    public static Rotation toRotation(double x, double y, double z) {
        return new Rotation(MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(z, x)) - 90),
          MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(y, Math.sqrt(x * x + z * z)))));
    }

    public static Rotation toRotation(Vec3 vec) {
        return toRotation(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    //thx lb
    public static double angleDiff(double a, double b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static int toMouse(double deg) {
        float var13 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float var14 = var13 * var13 * var13 * 8.0F;
        return (int) (deg / var14);
    }

    public static float toDeg(int mouse) {
        float var13 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float var14 = var13 * var13 * var13 * 8.0F;
        return mouse * var14;
    }

    public static boolean isBetweenAngles(double a, double b, double r) {
        double diff = Math.abs(angleDiff(a, b));
        return Math.abs(angleDiff(a, r)) <= diff & Math.abs(angleDiff(b, r)) <= diff;
    }

    public static double wrapDegrees(double a) {
        a = a % 360;
        if (a >= 180) {
            return a - 360;
        }
        if (a < -180) {
            return a + 360;
        }
        return a;
    }

    public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final double turnSpeed) {
        final double yawDifference = angleDiff(targetRotation.yaw, currentRotation.yaw);
        final double pitchDifference = angleDiff(targetRotation.pitch, currentRotation.pitch);

        return new Rotation(currentRotation.yaw + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
          currentRotation.pitch + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
    }

    public static Rotation rotationDiff(final Rotation a, final Rotation b) {
        return new Rotation(angleDiff(a.yaw, b.yaw), angleDiff(a.pitch, b.pitch));
    }

    public static class RotationInfo {
        public static final Random random = new Random();
        public static Rotation lastRandom;
        public final Rotation currentRotation;
        public final Rotation closestRotation;
        public final Rotation minRotation;
        public final Rotation maxRotation;

        public RotationInfo(Rotation currentRotation, Rotation closestRotation, Rotation minRotation, Rotation maxRotation) {
            this.currentRotation = currentRotation;
            this.closestRotation = closestRotation;
            this.minRotation = minRotation;
            this.maxRotation = maxRotation;
        }

        public Rotation getRotation(int mode, int pingPongHTime, int pingPongVTime) {
            switch (mode) {
                case 1:
                    return edge();
                case 2:
                    return random();
                case 3:
                    return pingPong(pingPongHTime, pingPongVTime);
                default:
                    return closestRotation;
            }
        }

        public Rotation diff() {
            return new Rotation(angleDiff(minRotation.yaw, maxRotation.yaw), angleDiff(minRotation.pitch, maxRotation.pitch));
        }

        public Rotation pingPong(int hTime, int vTime) {
            Rotation diff = diff();
            return new Rotation(minRotation.yaw - diff.yaw * time(hTime),
              minRotation.pitch - diff.pitch * time(vTime));
        }

        public Rotation random() {
            if (lastRandom != null && currentRotation.getDiffS(lastRandom) > 5) return lastRandom;
            Rotation diff = diff();
            return lastRandom = new Rotation(minRotation.yaw - diff.yaw * random.nextDouble(),
              minRotation.pitch - diff.pitch * random.nextDouble());
        }

        public Rotation edge() {
            double minYDiff = angleDiff(minRotation.yaw, closestRotation.yaw);
            double maxYDiff = angleDiff(maxRotation.yaw, closestRotation.yaw);
            if (minYDiff < maxYDiff) return new Rotation(minRotation.yaw, closestRotation.pitch);
            return new Rotation(maxRotation.yaw, closestRotation.pitch);
        }

        public static double time(double s) {
            double l = (System.currentTimeMillis() % s * 2);
            return (l > s ? -l + s * 2 : l) / s;
        }
    }
}
