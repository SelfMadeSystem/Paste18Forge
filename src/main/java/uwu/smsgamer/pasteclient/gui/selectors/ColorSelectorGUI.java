package uwu.smsgamer.pasteclient.gui.selectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import uwu.smsgamer.pasteclient.utils.*;
import uwu.smsgamer.pasteclient.values.ColorValue;

import java.awt.*;
import java.io.IOException;

public class ColorSelectorGUI extends GuiScreen {
    public static int getSliderWidth() {
        return getScreenResX() / 30;
    }

    public static int getScreenResX() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        return res.getScaledWidth();
    }

    public static int getScreenResY() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        return res.getScaledHeight();
    }

    public RGBSlider sliderR = new RGBSlider(0, this);
    public RGBSlider sliderG = new RGBSlider(1, this);
    public RGBSlider sliderB = new RGBSlider(2, this);
    public HSVSlider sliderH = new HSVSlider(0, this);
    public HSVSlider sliderS = new HSVSlider(1, this);
    public HSVSlider sliderV = new HSVSlider(2, this);
    public AlphaSlider sliderA = new AlphaSlider(this);

    public final GuiScreen prev;
    public final ColorValue value;
    public Color cursor;
    public Colour.HSV hsv;
    public Colour.RGB rgb;
    public double alpha;

    public ColorSelectorGUI(GuiScreen prev, ColorValue value) {
        this.prev = prev;
        this.value = value;
        Color color = value.getColor();
        this.rgb = new Colour.RGB(color);
        this.hsv = Colour.rgb2hsv(color);
        this.alpha = color.getAlpha() / 255D;
    }

    public void setRGB(Colour.RGB rgb) {
        this.rgb = rgb;
        this.hsv = Colour.rgb2hsv(rgb);
        this.value.setValue(rgb);
    }

    public void setHSV(Colour.HSV hsv) {
        this.hsv = hsv;
        this.rgb = Colour.hsv2rgb(hsv);
        this.value.setValue(hsv);
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
        this.value.setAlpha(alpha);
    }

    public void check() {
        if (!this.rgb.equals(this.value.getRGB())) setRGB(this.value.getRGB());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        cursor = (0.2126 * rgb.r + 0.7152 * rgb.g + 0.0722 * rgb.b < 0.5) ? Color.WHITE : Color.BLACK;
        int centerX = getScreenResX() / 2;
        int centerY = getScreenResY() / 2;
        drawRect(centerX - 50, centerY - 50, centerX + 50, centerY + 50, rgb.toColor(alpha).getRGB());
        sliderR.render(mouseX, mouseY);
        sliderG.render(mouseX, mouseY);
        sliderB.render(mouseX, mouseY);
        sliderH.render(mouseX, mouseY);
        sliderS.render(mouseX, mouseY);
        sliderV.render(mouseX, mouseY);
        sliderA.render(mouseX, mouseY);
        check();
    }

    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) {
        if (p_keyTyped_2_ == 1) {
            this.mc.displayGuiScreen(prev);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        sliderR.click(mouseX, mouseY, mouseButton, 0);
        sliderG.click(mouseX, mouseY, mouseButton, 0);
        sliderB.click(mouseX, mouseY, mouseButton, 0);
        sliderH.click(mouseX, mouseY, mouseButton, 0);
        sliderS.click(mouseX, mouseY, mouseButton, 0);
        sliderV.click(mouseX, mouseY, mouseButton, 0);
        sliderA.click(mouseX, mouseY, mouseButton, 0);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        sliderR.click(mouseX, mouseY, mouseButton, 1);
        sliderG.click(mouseX, mouseY, mouseButton, 1);
        sliderB.click(mouseX, mouseY, mouseButton, 1);
        sliderH.click(mouseX, mouseY, mouseButton, 1);
        sliderS.click(mouseX, mouseY, mouseButton, 1);
        sliderV.click(mouseX, mouseY, mouseButton, 1);
        sliderA.click(mouseX, mouseY, mouseButton, 1);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        sliderR.click(mouseX, mouseY, mouseButton, 2);
        sliderG.click(mouseX, mouseY, mouseButton, 2);
        sliderB.click(mouseX, mouseY, mouseButton, 2);
        sliderH.click(mouseX, mouseY, mouseButton, 2);
        sliderS.click(mouseX, mouseY, mouseButton, 2);
        sliderV.click(mouseX, mouseY, mouseButton, 2);
        sliderA.click(mouseX, mouseY, mouseButton, 2);
    }

    static abstract class Slider {
        public final ColorSelectorGUI gui;

        protected Slider(ColorSelectorGUI gui) {
            this.gui = gui;
        }

        public abstract void render(int mouseX, int mouseY);

        boolean selected = false;

        public void click(int mouseX, int mouseY, int mouseButton, int mouseMode) {
            if (mouseButton == 0 && mouseMode == 0 && selected(mouseX, mouseY))
                selected = true;
            if (mouseMode == 2) selected = false;
            if (selected) set((double) mouseY / getScreenResY());
        }

        public abstract boolean selected(int mouseX, int mouseY);

        public abstract void set(double d);
    }

    static class HSVSlider extends Slider {
        final int mode;

        HSVSlider(int mode, ColorSelectorGUI gui) {
            super(gui);
            this.mode = mode;
        }

        @Override
        public void render(int mouseX, int mouseY) {
            int height = getScreenResY();
            Colour.HSV hsv = gui.hsv.clone();
            for (int i = 0; i < height; i++) {
                double d = (double) i / height;
                switch (mode) {
                    case 0:
                        hsv.h = d * 360;
                        break;
                    case 1:
                        hsv.s = d;
                        break;
                    case 2:
                        hsv.v = d;
                        break;
                }
                Color color = gui.hsv.approxEquals(hsv) ? gui.cursor : hsv.toColor();
                drawRect(getSliderWidth() * mode, i, getSliderWidth() * (mode + 1), i + 1, color.getRGB());
            }
        }

        @Override
        public boolean selected(int mouseX, int mouseY) {
            return mouseX > getSliderWidth() * mode && mouseX < getSliderWidth() * (mode + 1);
        }

        @Override
        public void set(double d) {
            switch (mode) {
                case 0:
                    gui.hsv.h = d * 360;
                    break;
                case 1:
                    gui.hsv.s = d;
                    break;
                case 2:
                    gui.hsv.v = d;
                    break;
            }
            gui.setHSV(gui.hsv);
        }
    }

    static class RGBSlider extends Slider {
        final int mode;

        RGBSlider(int mode, ColorSelectorGUI gui) {
            super(gui);
            this.mode = mode;
        }

        public void render(int mouseX, int mouseY) {
            int width = getScreenResX();
            int height = getScreenResY();
            Colour.RGB rgb = gui.rgb.clone();
            for (int i = 0; i < height; i++) {
                double d = (double) i / height;
                switch (mode) {
                    case 0:
                        rgb.r = d;
                        break;
                    case 1:
                        rgb.g = d;
                        break;
                    case 2:
                        rgb.b = d;
                        break;
                }
                Color color = gui.rgb.approxEquals(rgb) ? gui.cursor : rgb.toColor();
                drawRect(width - getSliderWidth() * (mode), i, width - getSliderWidth() * (mode + 1), i + 1, color.getRGB());
            }
        }

        @Override
        public boolean selected(int mouseX, int mouseY) {
            int width = getScreenResX();
            return mouseX > width - getSliderWidth() * (mode + 1) && mouseX < width - getSliderWidth() * (mode);
        }

        @Override
        public void set(double d) {
            switch (mode) {
                case 0:
                    gui.rgb.r = d;
                    break;
                case 1:
                    gui.rgb.g = d;
                    break;
                case 2:
                    gui.rgb.b = d;
                    break;
            }
            gui.setRGB(gui.rgb);
        }
    }

    static class AlphaSlider extends Slider {

        AlphaSlider(ColorSelectorGUI gui) {
            super(gui);
        }

        @Override
        public void render(int mouseX, int mouseY) {
            Color color = gui.rgb.toColor();
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int xOffset = getSliderWidth() * 3;
            int width = getScreenResX() - getSliderWidth() * 6;
            int maxY = getScreenResY();
            int minY = getScreenResY() - getSliderWidth();
            for (int i = 0; i < width; i++)
                drawRect(xOffset + i, minY, xOffset + i + 1, maxY,
                  MathUtil.approxEquals(gui.alpha * width, i, 0.5) ? gui.cursor.getRGB() :
                    new Color(r, g, b, (int) (((double) i / width) * 255)).getRGB());
        }

        @Override
        public void click(int mouseX, int mouseY, int mouseButton, int mouseMode) {
            if (mouseButton == 0 && mouseMode == 0 && selected(mouseX, mouseY))
                selected = true;
            if (mouseMode == 2) selected = false;
            if (selected)
                set(Math.min(1, Math.max(0, (double) (mouseX - getSliderWidth() * 3) / (getScreenResX() - getSliderWidth() * 6))));
        }

        @Override
        public boolean selected(int mouseX, int mouseY) {
            return mouseX >= getSliderWidth() * 3 && mouseX <= getScreenResX() - getSliderWidth() * 3 &&
              mouseY > getScreenResY() - getSliderWidth() && mouseY < getScreenResY();
        }

        @Override
        public void set(double d) {
            gui.setAlpha(d);
        }
    }
}
