package uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens;

import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.*;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.components.*;
import uwu.smsgamer.pasteclient.modules.*;

public class ModuleScreen extends SesqScreen {
    public static int MAX_MOD_W = 3;
    public static int MOD_WIDTH = 200;
    public static int MOD_HEIGHT = 125;
    public static int SPACING = 20;
    public static int SPACING_TOP = 50;
    public static int SPACING_BOTTOM = 0; //here just encase

    public final ModuleCategory category;

    public ModuleScreen(SesqScreen prevScreen, ModuleCategory category) {
        super(prevScreen);
        this.category = category;
        reload();
    }

    @Override
    public void render(int mouseX, int mouseY, long deltaTime) {
        int catSize = components.size();
        destWidth = MAX_MOD_W * MOD_WIDTH + SPACING * (MAX_MOD_W + 1);
        destHeight = Math.ceil((double) catSize / MAX_MOD_W) * MOD_HEIGHT + Math.ceil((double) catSize / MAX_MOD_W) * SPACING + SPACING_TOP + SPACING_BOTTOM;
        super.render(mouseX, mouseY, deltaTime);
        if ((1 - animationThrough()) > 0.7) {
            renderer.setOpacity(((1 - animationThrough()) - 0.7) * 10 / 3);
            double offsetX = ((MAX_MOD_W * MOD_WIDTH + SPACING * (MAX_MOD_W - 1)) / 2D);
            double offsetY = ((Math.ceil((double) catSize / MAX_MOD_W) * MOD_HEIGHT +
              Math.ceil((double) catSize / MAX_MOD_W - 2) * SPACING + SPACING_BOTTOM) / 2D);
            int posX = getMidScreenX();
            int posY = getMidScreenY();
            for (int i = 0; i < components.size(); i++) {
                int iX = i % MAX_MOD_W;
                int iY = i / MAX_MOD_W;
                components.get(i).render((iX * SPACING + iX * MOD_WIDTH) - offsetX + posX,
                  (iY * SPACING + iY * MOD_HEIGHT) - offsetY + posY, mouseX, mouseY, deltaTime);
            }
        }
        renderer.setOpacity(1);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        super.click(mouseX, mouseY, mouseButton, pressType);
        for (SesqComponent component : components) {
            component.click(mouseX, mouseY, mouseButton, pressType);
        }
    }

    @Override
    public void reload() {
        this.components.clear();
        for (Module module : PasteClient.INSTANCE.moduleManager.getModules(category)) {
            components.add(new SesqModule(this, module));
        }
    }
}
