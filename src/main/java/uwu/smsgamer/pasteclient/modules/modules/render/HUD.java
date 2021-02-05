/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.modules.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.events.*;
import uwu.smsgamer.pasteclient.gui.tabgui.*;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.modules.modules.render.hud.*;
import uwu.smsgamer.pasteclient.notifications.NotificationManager;
import uwu.smsgamer.pasteclient.utils.GLUtil;
import uwu.smsgamer.pasteclient.values.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HUD extends PasteModule {
    @NotNull
    private final TabGui<PasteModule> tabGui = new TabGui<>();
    @NotNull
    private final List<Integer> fps = new ArrayList<>();
    public final List<Long> cps = new ArrayList<>();
    public final double accuracy = 0;
    public final double lastRange = 0;

    @NotNull
    public final NumberValue fpsStatisticLength = addInt("FPSStatisticLength", "idk", 250D, 10D, 500D);
    public final BoolValue eTabGUI = addBool("TabGui", "Enable tab gui", true);
    public final BoolValue eStatFPS = addBool("StatFPS", "Enable stat frames per second", true);
    public final BoolValue eStatBPS = addBool("StatBPS", "Enable stat blocks per second", true);
    public final BoolValue eStatCPS = addBool("StatCPS", "Enable stat blocks per second", false);
    public final BoolValue eStatAAc = addBool("StatAA", "Enable stat aim accuracy", false);
    public final BoolValue eStatRan = addBool("StatRange", "Enable stat hit range", false);

    private final HudStat[] stats = new HudStat[]{
      new FPSStat(),
      new BPSStat(),
      new CPSStat(),
      new RanStat(),
      new AAStat()
    };

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    public HUD() {
        super("HUD", "The Overlay", ModuleCategory.RENDER, true, false, Keyboard.KEY_NONE);
        setState(true);


        HashMap<ModuleCategory, java.util.List<PasteModule>> moduleCategoryMap = new HashMap<>();

        for (PasteModule module : PasteClient.INSTANCE.moduleManager.getModules()) {
            if (!moduleCategoryMap.containsKey(module.getCategory())) {
                moduleCategoryMap.put(module.getCategory(), new ArrayList<>());
            }

            moduleCategoryMap.get(module.getCategory()).add(module);
        }

        moduleCategoryMap.entrySet().stream().sorted(Comparator.comparingInt(cat -> cat.getKey().toString().hashCode())).forEach(cat -> {
            Tab<PasteModule> tab = new Tab<>(cat.getKey().toString());

            for (PasteModule module : cat.getValue()) {
                tab.addSubTab(new SubTab<>(module.getName(), subTab -> subTab.getObject().setState(!subTab.getObject().getState()), module));
            }

            tabGui.addTab(tab);
        });

    }

    private static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f).getRGB();
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {

    }

    @EventTarget
    private void render2D(Render2DEvent event) {
        if (!getState()) return;

        fps.add(Minecraft.getDebugFPS());
        while (fps.size() > fpsStatisticLength.getInt()) {
            fps.remove(0);
        }

        while (!cps.isEmpty() && cps.get(0) < System.currentTimeMillis() - 1000) cps.remove(0);

        FontRenderer fontRenderer = mc.fontRendererObj;

        ScaledResolution res = new ScaledResolution(mc);

        int blackBarHeight = fontRenderer.FONT_HEIGHT * 2 + 4;

        Gui.drawRect(0, res.getScaledHeight() - blackBarHeight, res.getScaledWidth(), res.getScaledHeight(), GLUtil.toRGBA(new Color(0, 0, 0, 150)));

        GL11.glScaled(2.0, 2.0, 2.0);
        int i = fontRenderer.drawString(PasteClient.CLIENT_NAME, 2, 2, rainbow(0), true);
        int initialSize = fontRenderer.drawString(PasteClient.CLIENT_INITIALS, 1, res.getScaledHeight() / 2.0f - fontRenderer.FONT_HEIGHT, rainbow(0), true);
        GL11.glScaled(0.5, 0.5, 0.5);

        fontRenderer.drawString(PasteClient.CLIENT_VERSION, i * 2, fontRenderer.FONT_HEIGHT * 2 - 7, rainbow(100), true);
        fontRenderer.drawString("by " + PasteClient.CLIENT_AUTHOR, 4, fontRenderer.FONT_HEIGHT * 2 + 2, rainbow(200), true);

        int maxWidth = initialSize * 2 - 6;
        int lastWidth = 0;

        boolean down = false;

        for (HudStat stat : stats) {
            if (!stat.render(this)) return;
            if (down = !down)
                lastWidth = fontRenderer.drawString(stat.getStatString(this), maxWidth + 8, res.getScaledHeight() - fontRenderer.FONT_HEIGHT * 2 - 2, -1, true);
            else {
                int width = fontRenderer.drawString(stat.getStatString(this), maxWidth + 8, res.getScaledHeight() - fontRenderer.FONT_HEIGHT - 2, -1, true);
                maxWidth += Math.max(width, lastWidth) - maxWidth;
                lastWidth = 0;
            }
        }

        if (lastWidth != 0) maxWidth += lastWidth - maxWidth;

//        fontRenderer.drawString("FPS: " + Minecraft.getDebugFPS(), initialSize * 2 + 2, res.getScaledHeight() - fontRenderer.FONT_HEIGHT - 2, -1, true);
//        maxWidth = Math.max(maxWidth, fontRenderer.drawString(String.format("BPS: %.2f", currSpeed), initialSize * 2 + 2, res.getScaledHeight() - fontRenderer.FONT_HEIGHT * 2 - 2, -1, true));


        LocalDateTime now = LocalDateTime.now();
        String date = dateFormat.format(now);
        String time = timeFormat.format(now);


        fontRenderer.drawString(date, res.getScaledWidth() - fontRenderer.getStringWidth(date), res.getScaledHeight() - fontRenderer.FONT_HEIGHT - 2, -1, true);
        fontRenderer.drawString(time, res.getScaledWidth() - fontRenderer.getStringWidth(time), res.getScaledHeight() - fontRenderer.FONT_HEIGHT * 2 - 2, -1, true);


        AtomicInteger offset = new AtomicInteger(3);
        AtomicInteger index = new AtomicInteger();

        PasteClient.INSTANCE.moduleManager.getModules().stream().filter(mod -> mod.getState() && !mod.isHidden()).sorted(Comparator.comparingInt(mod -> -fontRenderer.getStringWidth(mod.getName()))).forEach(mod -> {
            fontRenderer.drawString(mod.getName(), res.getScaledWidth() - fontRenderer.getStringWidth(mod.getName()) - 3, offset.get(), rainbow(index.get() * 100), true);

            offset.addAndGet(fontRenderer.FONT_HEIGHT + 2);
            index.getAndIncrement();
        });

        NotificationManager.render();
        if (eTabGUI.getValue()) tabGui.render(5, (2 + fontRenderer.FONT_HEIGHT) * 3);


        int max = fps.stream().max(Integer::compareTo).orElse(1);
        double transform = blackBarHeight / 2.0 / (double) max;

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(1.0f);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glBegin(GL11.GL_LINE_STRIP);

        maxWidth += 3;

        double v = ((res.getScaledWidth() / 2.0 - 100) - maxWidth) / (double) fps.size();

        for (int j = 0; j < fps.size(); j++) {
            int currFPS = fps.get(j);

            GL11.glVertex2d(maxWidth + j * v, res.getScaledHeight() - transform * currFPS);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @EventTarget
    public void onKey(@NotNull KeyEvent event) {
        if (eTabGUI.getValue()) tabGui.handleKey(event.getKey());
    }

    @EventTarget
    public void onSwing(SwingArmEvent event) {
        if (event.getEventType().equals(EventType.PRE)) {
            cps.add(System.currentTimeMillis());
        }
    }
}
