package uwu.smsgamer.pasteclient.gui.clickgui.utils;

import org.lwjgl.opengl.GL11;
import uwu.smsgamer.pasteclient.utils.GLUtil;
import uwu.smsgamer.pasteclient.utils.fontRenderer.GlyphPageFontRenderer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL41.glClearDepthf;

public class ClientBaseRendererImpl implements IRenderer {
    private final GlyphPageFontRenderer renderer;
    private double opacity;

    public ClientBaseRendererImpl(GlyphPageFontRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void setOpacity(double o) {
        opacity = o;
    }

    @Override
    public void drawRect(double x, double y, double w, double h, Color c) {
        GLUtil.drawRect(GL11.GL_QUADS, x / 2.0, y / 2.0, x / 2.0 + w / 2.0, y / 2.0 + h / 2.0, toRGBA(c));
    }

    @Override
    public void drawRectR(double x1, double y1, double x2, double y2, Color c) {
        GLUtil.drawRect(GL11.GL_QUADS, x1 / 2.0, y1 / 2.0, x2 / 2.0, y2 / 2.0, toRGBA(c));
    }

    @Override
    public void drawRoundedRect(double x, double y, double w, double h, double r, Color c) {
        y /= 2;
        x /= 2;
        w /= 2;
        h /= 2;
        GLUtil.drawRoundRect(x, y, x + w, y + h, r, c);
    }

    @Override
    public void drawCircle(double x, double y, double r, double ir, float sta, float spa, float ext, float add, Color c) {
        y /= 2;
        x /= 2;
        r /= 2;
        ir /= 2;
        GLUtil.drawCircle(x, y, r, ir, sta, spa, ext, add, c);
    }

    @Override
    public void drawOutline(double x, double y, double w, double h, float lineWidth, Color c) {
//        GL11.glLineWidth(lineWidth);
//        GLUtil.drawRect(GL11.GL_LINE_LOOP, x / 2.0, y / 2.0, x / 2.0 + w / 2.0, y / 2.0 + h / 2.0, toRGBA(c));
        y /= 2;
        x /= 2;
        w /= 2;
        h /= 2;
        lineWidth /= 2;
        GLUtil.drawRect(GL11.GL_QUADS, x, y, x + w, y + lineWidth, toRGBA(c));
        GLUtil.drawRect(GL11.GL_QUADS, x, y, x + lineWidth, y + h, toRGBA(c));
        GLUtil.drawRect(GL11.GL_QUADS, x, y + h - lineWidth, x + w, y + h, toRGBA(c));
        GLUtil.drawRect(GL11.GL_QUADS, x + w - lineWidth, y, x + w, y + h, toRGBA(c));
    }

    @Override
    public void setColor(Color c) {
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha() * opacity));
        GLUtil.setColor(c);
    }

    @Override
    public void drawString(int x, int y, String text, Color color) {
        renderer.drawString(text, x / 2f, y / 2f, toRGBA(color), false);
    }

    @Override
    public int getStringWidth(String str) {
        return renderer.getStringWidth(str) * 2;
    }

    @Override
    public int getStringHeight(String str) {
        return renderer.getFontHeight() * 2;
    }

    @Override
    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder worldrenderer = tessellator.getBuffer();
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture2D();
//        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//
//        setColor(color);
//
//        worldrenderer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION);
//        worldrenderer.pos(x1, y1, 0.0D).endVertex();
//        worldrenderer.pos(x2, y2, 0.0D).endVertex();
//        worldrenderer.pos(x3, y3, 0.0D).endVertex();
//        tessellator.draw();
//        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
    }

    @Override
    public void initMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT);
        glColorMask(false, false, false, false);
        glDepthFunc(GL_LESS);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    @Override
    public void useMask() {
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glDepthFunc(GL_EQUAL);
    }

    @Override
    public void disableMask() {
        glClearDepthf(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
    }

    public int toRGBA(Color color) {
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
        return color.getRGB();
    }
}
