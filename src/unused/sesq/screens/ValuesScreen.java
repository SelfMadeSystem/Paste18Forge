package uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens;

import uwu.smsgamer.pasteclient.gui.clickgui.sesq.*;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.values.*;
import uwu.smsgamer.pasteclient.gui.clickgui.utils.*;
import uwu.smsgamer.pasteclient.modules.Module;
import uwu.smsgamer.pasteclient.utils.fontRenderer.GlyphPageFontRenderer;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;
import java.util.*;

public class ValuesScreen extends SesqScreen {
    public static final IRenderer bigFont;

    static {
        GlyphPageFontRenderer consolas = GlyphPageFontRenderer.create("Montserrat", 30, true, false, false);
        bigFont = new ClientBaseRendererImpl(consolas);
    }

    public static int SPACING = 10;
    public static int TOP_SPACING = 55;
    public static int BOTTOM_SPACING = 100;
    public static int WIDTH = 700;
    public static int WIDTH_ADD = 20;

    public final Module module;
    public final Value<?> value;

    public ValuesScreen(SesqScreen prevScreen, Module module) {
        super(prevScreen);
        this.module = module;
        this.value = null;
        reload();
    }

    public ValuesScreen(SesqScreen prevScreen, Value<?> value) {
        super(prevScreen);
        this.value = value;
        this.module = null;
        reload();
    }

    @Override
    public void reload() {
        this.components.clear();
        Collection<Value<?>> values = module == null ? value.getChildren().values() : module.getValues().values();
        for (Value<?> value : values) {
            if (value instanceof BoolValue) {
                components.add(new SesqBool(this, (BoolValue) value));
            } else if (value instanceof NumberValue) {
                components.add(new SesqSlider(this, (NumberValue) value));
            } else {
                components.add(new SesqMisc(this, value));
            }
        }
        destWidth = WIDTH + WIDTH_ADD * 2;
        destHeight = 0;
        {
            boolean switcher = true;
            double height1 = TOP_SPACING;
            double height2 = TOP_SPACING;
            for (Iterator<SesqComponent> iterator = this.components.iterator(); iterator.hasNext(); ) {
                SesqComponent component = iterator.next();
                if (switcher) {
                    height1 += component.getHeight() + (iterator.hasNext() ? SPACING : BOTTOM_SPACING);
                    switcher = false;
                } else {
                    height2 += component.getHeight() + (iterator.hasNext() ? SPACING : BOTTOM_SPACING);
                    switcher = true;
                }
            }
            destHeight = Math.max(height1, height2);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, long deltaTime) {
        super.render(mouseX, mouseY, deltaTime);
        if ((1 - animationThrough()) > 0.7) {
            renderer.setOpacity(((1 - animationThrough()) - 0.7) * 10 / 3);
            bigFont.setOpacity(((1 - animationThrough()) - 0.7) * 10 / 3);
            int compCount = components.size() / 2;
            double currentX = getMidScreenX() - WIDTH / 2D;
            double currentY = getMidScreenY() - getHeight() / 2 + TOP_SPACING;
            boolean swapped = false;
            for (int i = 0; i < components.size(); i++) {
                SesqComponent component = components.get(i);
                if (i > compCount && !swapped) {
                    currentX = getMidScreenX();
                    currentY = getMidScreenY() - getHeight() / 2 + TOP_SPACING;
                    swapped = true;
                }
                component.render(currentX, currentY, mouseX, mouseY, deltaTime);
                currentY += component.getHeight() + SPACING;
            }
            int sWidth = renderer.getStringWidth(getName());
            // FIXME: 2020-11-18 not centered
            bigFont.drawString(getMidScreenX() - sWidth/2, (int) (getMidScreenY() - getHeight()/2 - 4), getName(), Color.BLACK);
            renderer.setOpacity(1);
            bigFont.setOpacity(1);
        }
    }

    public String getName() {
        return module == null ? value.getName() : module.getName();
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
        for (SesqComponent component : components) {
            component.click(mouseX, mouseY, mouseButton, pressType);
        }
    }
}
