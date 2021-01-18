package uwu.smsgamer.pasteclient.modules.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.injection.interfaces.IMixinMinecraft;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.utils.*;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;

public class Tracers extends PasteModule {
    public FancyColorValue color = (FancyColorValue) addValue(new FancyColorValue("Color", "Color for ESP.", Color.WHITE));
    public NumberValue lineWidth = (NumberValue) addValue(new NumberValue("LineWidth", "Width of the line.", 2, 0.1, 10, 0.1, NumberValue.NumberType.DECIMAL));

    public Tracers() {
        super("Tracers", "Draws a line between entities and the center of your screen", ModuleCategory.RENDER);
    }

    // This really didn't need to be skidded from LB.
    // I already knew how to do it, just I was rendering on 2D instead of 3D LOL
    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (!getState()) return;
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(lineWidth.getValue().floatValue());
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glBegin(GL11.GL_LINES);

        for (Entity e : mc.theWorld.loadedEntityList) if (TargetUtil.isValid(e)) drawTraces(e);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void drawTraces(Entity entity) {
        float partialTicks = ((IMixinMinecraft)mc).getTimer().renderPartialTicks;
        double x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks
          - mc.getRenderManager().viewerPosX);
        double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks
          - mc.getRenderManager().viewerPosY);
        double z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks
          - mc.getRenderManager().viewerPosZ);

        Vec3 eyeVector = new Vec3(0.0, 0.0, 1.0)
          .rotatePitch((float) -Math.toRadians(mc.thePlayer.rotationPitch))
          .rotateYaw((float) -Math.toRadians(mc.thePlayer.rotationYaw));

        GLUtil.glColor(color.getColor());

        GL11.glVertex3d(eyeVector.xCoord, mc.thePlayer.eyeHeight + eyeVector.yCoord, eyeVector.zCoord);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y + entity.height, z);
    }
}
