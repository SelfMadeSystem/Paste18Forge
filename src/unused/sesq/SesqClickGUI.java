package uwu.smsgamer.pasteclient.gui.clickgui.sesq;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens.CategoryScreen;
import uwu.smsgamer.pasteclient.gui.clickgui.utils.*;
import uwu.smsgamer.pasteclient.utils.fontRenderer.GlyphPageFontRenderer;

import java.io.IOException;

//Sigma ESQue
public class SesqClickGUI extends GuiScreen {
    // TODO: 2020-11-17 COLOURS UWU

    // Renderer to use :)
    public static final IRenderer renderer;

    static {
        GlyphPageFontRenderer consolas = GlyphPageFontRenderer.create("Montserrat", 15, false, false, false);
        renderer = new ClientBaseRendererImpl(consolas);
    }

    private static SesqClickGUI instance;
    public static SesqClickGUI getInstance() {
      if (instance == null) new SesqClickGUI();
      return instance;
    }

    private SesqClickGUI() {
        if (instance != null) throw new InternalError("WHYYYYYYYYYYY");
        instance = this;
        currentScreen = new CategoryScreen(); //do this here because instance is still null if we do it in initialization
    }

    // The current screen to render.
    private SesqScreen currentScreen;

    public SesqScreen getCurrentScreen() {
        return this.currentScreen;
    }

    /**
     * Sets "currentScreen" to the new screen and sets its
     * init width & height to the old screen's and sets the
     * animation time to zero to start the noice animation.
     * @param newScreen The screen to set.
     */
    public void setCurrentScreen(SesqScreen newScreen) {
        newScreen.initWidth = this.currentScreen.destWidth;
        newScreen.initHeight = this.currentScreen.destHeight;
        newScreen.animationTime = 0;
        newScreen.reload();
        this.currentScreen = newScreen;
    }

    /**
     * Gets the middle of the screen on the X axs.
     * @return the middle of the screen on the X axs.
     */
    public static int getMidScreenX() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        return res.getScaledWidth();
    }

    /**
     * Gets the middle of the screen on the Y axs.
     * @return the middle of the screen on the Y axs.
     */
    public static int getMidScreenY() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        return res.getScaledHeight();
    }

    private long lastRender = System.currentTimeMillis();

    /**
     * Draws the screen.
     * @param mouseX The X mouse position.
     * @param mouseY The Y mouse position.
     * @param partialTicks The partial ticks. Not used.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        long now = System.currentTimeMillis();
        long deltaTime = now - lastRender;
        currentScreen.render(mouseX, mouseY, deltaTime);
        lastRender = System.currentTimeMillis();
    }

    /**
     * Function to do when the mouse is clicked. Just sends it to the current screen with press type 0.
     * @param mouseX The X mouse position.
     * @param mouseY The Y mouse position.
     * @param mouseButton 0 left - 1 right - 2 middle
     * @throws IOException idk why
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        currentScreen.click(mouseX, mouseY, mouseButton, 0);
    }

    /**
     * Function to do when the mouse is moving. Just sends it to the current screen with press type 1.
     * @param mouseX The X mouse position.
     * @param mouseY The Y mouse position.
     * @param mouseButton 0 left - 1 right - 2 middle
     * @param timeSinceLastClick Time since last click in ms?
     */
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        currentScreen.click(mouseX, mouseY, mouseButton, 1);
    }

    /**
     * Function to do when the mouse is released. Just sends it to the current screen with press type 2.
     * @param mouseX The X mouse position.
     * @param mouseY The Y mouse position.
     * @param mouseButton The button that was pressed. 0 left - 1 right - 2 middle
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        currentScreen.click(mouseX, mouseY, mouseButton, 2);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        if (keyCode == 1) { // TODO: 2020-11-18 send this to current screen
            close();
        }
    }

    /**
     * Closes the GUI and resets current screen to category screen.
     */
    public void close() {
        this.currentScreen = new CategoryScreen();
        this.currentScreen.animationTime = SesqScreen.ANIMATION_TIME;
        this.mc.displayGuiScreen(null);
        if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
        }
    }
}
