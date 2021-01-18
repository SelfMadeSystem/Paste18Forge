package uwu.smsgamer.pasteclient.modules.modules.render.esp;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.modules.modules.render.ESP;
import uwu.smsgamer.pasteclient.utils.*;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class OutlinedCylinderESP extends ESPModule {
    @Override
    public void onRender(Render3DEvent event, Color c) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(ESP.getInstance().lineWidth.getValue().floatValue());
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        GLUtil.glColor(c);
        for (Entity e : mc.theWorld.loadedEntityList) if (TargetUtil.isValid(e)) esp(e);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public void esp(Entity e) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferBuilder = tessellator.getWorldRenderer();

        bufferBuilder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);

        // Draw here
        AxisAlignedBB aabb = GLUtil.getAxisAlignedBBRel(e.getEntityBoundingBox());
        Vec3 center = MathUtil.getCenter(aabb);
        double sizeX = aabb.maxX - aabb.minX;
        double sizeZ = aabb.maxZ - aabb.minZ;

        int add = 10;

        for (int i = 0; i < 360; i += add) {
            bufferBuilder.pos(center.xCoord + MathUtil.cos(i) * sizeX, aabb.minY, center.zCoord + MathUtil.sin(i) * sizeZ).endVertex();
            bufferBuilder.pos(center.xCoord + MathUtil.cos(i + add) * sizeX, aabb.minY, center.zCoord + MathUtil.sin(i + add) * sizeZ).endVertex();

            bufferBuilder.pos(center.xCoord + MathUtil.cos(i) * sizeX, aabb.minY, center.zCoord + MathUtil.sin(i) * sizeZ).endVertex();
            bufferBuilder.pos(center.xCoord + MathUtil.cos(i) * sizeX, aabb.maxY, center.zCoord + MathUtil.sin(i) * sizeZ).endVertex();

            bufferBuilder.pos(center.xCoord + MathUtil.cos(i) * sizeX, aabb.maxY, center.zCoord + MathUtil.sin(i) * sizeZ).endVertex();
            bufferBuilder.pos(center.xCoord + MathUtil.cos(i + add) * sizeX, aabb.maxY, center.zCoord + MathUtil.sin(i + add) * sizeZ).endVertex();
        }

        // End drawing

        tessellator.draw();
    }
}
