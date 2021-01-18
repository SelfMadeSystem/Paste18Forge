package uwu.smsgamer.pasteclient.utils;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketUtils {
    public static class CPacketPlayerBuilder extends C03PacketPlayer {
        public CPacketPlayerBuilder() {
        }

        public CPacketPlayerBuilder(Entity entity, boolean move, boolean rotation) {
            if (move) {
                this.x = entity.posX;
                this.y = entity.posY;
                this.z = entity.posZ;
            }
            if (rotation) {
                this.yaw = entity.rotationYaw;
                this.pitch = entity.rotationPitch;
            }
            this.onGround = entity.onGround;
        }

        public CPacketPlayerBuilder(C03PacketPlayer base) {
            if (base.getClass().equals(C06PacketPlayerPosLook.class)) {
                setMove(base);
                setRot(base);
            } else if (base.getClass().equals(C04PacketPlayerPosition.class)) {
                setMove(base);
            } else if (base.getClass().equals(C05PacketPlayerLook.class)) {
                setRot(base);
            }
            this.onGround = base.isOnGround();
        }

        private void setMove(C03PacketPlayer base) {
            this.moving = true;
            this.x = base.getPositionX();
            this.y = base.getPositionY();
            this.z = base.getPositionZ();
        }

        private void setRot(C03PacketPlayer base){
            this.rotating = true;
            this.yaw = base.getYaw();
            this.pitch = base.getPitch();
        }

        public CPacketPlayerBuilder setX(double x) {
            this.x = x;
            this.moving = true;
            return this;
        }

        public CPacketPlayerBuilder setY(double y) {
            this.y = y;
            this.moving = true;
            return this;
        }

        public CPacketPlayerBuilder setZ(double z) {
            this.z = z;
            this.moving = true;
            return this;
        }

        public CPacketPlayerBuilder setYaw(float yaw) {
            this.yaw = yaw;
            this.rotating = true;
            return this;
        }

        public CPacketPlayerBuilder setPitch(float pitch) {
            this.pitch = pitch;
            this.rotating = true;
            return this;
        }

        public CPacketPlayerBuilder setOnGround(boolean onGround) {
            this.onGround = onGround;
            return this;
        }

        public C03PacketPlayer build() {
            if (moving) {
                if (rotating) {
                    return new C06PacketPlayerPosLook(x, y, z, yaw, pitch, onGround);
                }
                return new C04PacketPlayerPosition(x, y, z, onGround);
            } else if (rotating) {
                return new C05PacketPlayerLook(yaw, pitch, onGround);
            } else {
                return new C03PacketPlayer(onGround);
            }
        }
    }
}
