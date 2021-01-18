package uwu.smsgamer.pasteclient.modules.modules.render.esp;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import uwu.smsgamer.pasteclient.events.Render3DEvent;
import uwu.smsgamer.pasteclient.modules.modules.render.ESP;
import uwu.smsgamer.pasteclient.utils.*;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class OutlineBoxESP extends ESPModule {
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

        bufferBuilder.begin(GL_LINES, DefaultVertexFormats.POSITION);

        // Draw here

        AxisAlignedBB aabb = GLUtil.getAxisAlignedBBRel(e.getEntityBoundingBox());

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();

        bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();


        // End drawing

        tessellator.draw();
    }
}
