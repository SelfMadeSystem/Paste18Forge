package uwu.smsgamer.pasteclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;

public class CustomMouseHelper {
    public int deltaX, deltaY;
    public float sensitivity;
    public float yaw, pitch;
    public float prevYaw, prevPitch;

    public void reinitialize(){
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        yaw = player.rotationYaw;
        pitch = player.rotationPitch;
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        sensitivity = settings.mouseSensitivity;
    }

    public void rotate() {
        float f = sensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        float f2 = deltaX * f1;
        float f3 = deltaY * f1;
        deltaX = 0;
        deltaY = 0;

        setAngles(f2, f3);
    }

    public void setAngles(float yaw, float pitch)
    {
        float f = this.pitch;
        float f1 = this.yaw;
        this.yaw = (float)(this.yaw + yaw);// * 0.15D);
        this.pitch = (float)(this.pitch - pitch);// * 0.15D);
        this.pitch = MathHelper.clamp_float(this.pitch, -90.0F, 90.0F);
        this.prevPitch += this.pitch - f;
        this.prevYaw += this.yaw - f1;
    }

    public Rotation toRotation() {
        return new Rotation(this.yaw, this.pitch);
    }

    public void toPlayer() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.rotationYaw = yaw;
        player.rotationPitch = pitch;
    }
}
