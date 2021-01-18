package uwu.smsgamer.pasteclient.gui.clickgui.sesq;

import uwu.smsgamer.pasteclient.gui.clickgui.utils.IRenderer;

import java.awt.*;
import java.util.LinkedList;

public abstract class SesqScreen {
    // GUI instance
    public static final SesqClickGUI gui = SesqClickGUI.getInstance();
    // Renderer instance
    public static final IRenderer renderer = SesqClickGUI.renderer;
    public static long ANIMATION_TIME = 1000;
    public static final String BACK_STRING = "<-";// TODO: 2020-11-18 actually make this good

    // The instance of the previous screen so you can go back to it.
    public final SesqScreen prevScreen;

    protected SesqScreen(SesqScreen prevScreen) {
        this.prevScreen = prevScreen;
    }

    /**
     * Gets the middle of the screen on the X axs.
     * @return the middle of the screen on the X axs.
     */
    public static int getMidScreenX() {
        return SesqClickGUI.getMidScreenX();
    }

    /**
     * Gets the middle of the screen on the Y axs.
     * @return the middle of the screen on the Y axs.
     */
    public static int getMidScreenY() {
        return SesqClickGUI.getMidScreenY();
    }

    // A list of components this screen has.
    public LinkedList<SesqComponent> components = new LinkedList<>();

    // Destination size.
    public double destWidth;
    public double destHeight;
    // Size of the previous screen.
    public double initWidth = 0;
    public double initHeight = 0;
    // How long the animation has been going on for (maxes out at ANIMATION_TIME)
    public long animationTime;

    /**
     * Returns the width by calculating the destination width plus
     * the difference between the previous screen's width and the
     * destination width multiplied by {@code animationThrough}.
     * <p>
     * destWidth + ((initWidth - destWidth) * animationThrough())
     * @return The screen's current width.
     */
    public double getWidth() {
        return destWidth + (initWidth - destWidth) * animationThrough();
    }

    /**
     * Returns the height by calculating the destination height plus
     * the difference between the previous screen's height and the
     * destination height multiplied by {@code animationThrough}.
     * <p>
     * destHeight + ((initHeight - destHeight) * animationThrough())
     * @return The screen's current height.
     */
    public double getHeight() {
        return destHeight + (initHeight - destHeight) * animationThrough();
    }

    /**
     * Returns a {@code double} from 0-1 signifying the remainder of the time for the animation. Not well phrased.
     * 1 means no time has passed, 0 means animation is done.
     * @return a number from 0-1 signifying the remainder of the time for the animation.
     */
    public double animationThrough() {
        return ((ANIMATION_TIME - animationTime) / (double) ANIMATION_TIME);
    }

    public void render(int mouseX, int mouseY, long deltaTime) {
        if (animationTime < ANIMATION_TIME) animationTime += deltaTime;
        if (animationTime > ANIMATION_TIME) animationTime = ANIMATION_TIME;
        renderer.drawRoundedRect(getMidScreenX() - getWidth() / 2, getMidScreenY() - getHeight() / 2, getWidth(), getHeight(), 20, Color.WHITE);
        renderer.drawString((int) (getMidScreenX() - getWidth() / 2 + 20), (int) (getMidScreenY() - getHeight() / 2 + 10), BACK_STRING,
          isMouseOverBack(mouseX, mouseY) ? Color.DARK_GRAY : Color.BLACK);
    }

    /**
     * {@code reload()} is intended to reset the {@code components}
     * with new components encase something has changed.
     */
    public abstract void reload();

    /**
     * Closes this screen. Usually just sets the gui's current screen to the previous screen,
     * but closes the GUI in the case of {@link uwu.smsgamer.pasteclient.gui.clickgui.sesq.screens.CategoryScreen}.
     */
    public void close() {
        gui.setCurrentScreen(prevScreen);
    }

    /**
     * Called when mouse event gets triggered. Generally passes down to each component, but doesn't have to.
     * @param mouseX The mouse position on the X axis.
     * @param mouseY The mouse position on the Y axis.
     * @param mouseButton The button that was pressed. 0 left - 1 right - 2 middle
     * @param pressType The press type. 0 press - 1 move - 2 release
     */
    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (isMouseOverBack(mouseX, mouseY) && mouseButton == 0 && pressType == 0) close();
    }

    /**
     * Used to see if the mouse is hovering over the back button.
     * @param mouseX The mouse position on the X axis.
     * @param mouseY The mouse position on the Y axis.
     * @return if the mouse is over the back button.
     */
    public boolean isMouseOverBack(int mouseX, int mouseY) {
        int sW = renderer.getStringWidth(BACK_STRING);
        int sH = renderer.getStringHeight(BACK_STRING);
        mouseX *= 2;
        mouseY *= 2;
        double minX = getMidScreenX() - getWidth() / 2 + 20;
        double minY = getMidScreenY() - getHeight() / 2 + 10;
        double maxX = getMidScreenX() - getWidth() / 2 + 20 + sW;
        double maxY = getMidScreenY() - getHeight() / 2 + 10 + sH;
        return mouseX < maxX && mouseX > minX && mouseY < maxY && mouseY > minY;
    }
}
