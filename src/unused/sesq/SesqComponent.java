package uwu.smsgamer.pasteclient.gui.clickgui.sesq;

import uwu.smsgamer.pasteclient.gui.clickgui.utils.IRenderer;

public abstract class SesqComponent {
    // GUI instance
    public static final SesqClickGUI gui = SesqClickGUI.getInstance();
    // Renderer instance
    public static final IRenderer renderer = SesqClickGUI.renderer;

    // Screen associated w/ this.
    public final SesqScreen screen;

    protected SesqComponent(SesqScreen screen) {
        this.screen = screen;
    }

    /**
     * Renders this component.
     * @param x The X position (left)
     * @param y The Y position (top)
     * @param mouseX The mouse position on the X axis.
     * @param mouseY The mouse position on the Y axis.
     * @param deltaTime The time since last render in ms.
     */
    public abstract void render(double x, double y, int mouseX, int mouseY, long deltaTime);

    /**
     * Called when mouse event gets triggered.
     * @param mouseX The mouse position on the X axis.
     * @param mouseY The mouse position on the Y axis.
     * @param mouseButton The button that was pressed. 0 left - 1 right - 2 middle
     * @param pressType The press type. 0 press - 1 move - 2 release
     */
    public abstract void click(int mouseX, int mouseY, int mouseButton, int pressType);

    /**
     * Gets the width of this component.
     * @return The width of this component.
     */
    public abstract double getWidth();

    /**
     * Gets the height of this component.
     * @return The height of this component.
     */
    public abstract double getHeight();

    public double lastX, lastY;

    /**
     * Sets the lastX and lastY.
     * @param x The current X coord.
     * @param y The current Y coord.
     */
    public void setLast(double x, double y) {
        lastX = x;
        lastY = y;
    }

    /**
     * Check to see if the mouse is hovering over this component.
     * @param mouseX The mouse's position on the X axis.
     * @param mouseY The mouse's position on the Y axis.
     * @return If the mouse is hovering over this component.
     */
    public boolean isHovering(int mouseX, int mouseY) {
        return isHovering(lastX, lastY, mouseX, mouseY);
    }


    /**
     * Check to see if the mouse is hovering over this component.
     * @param x The components position on the X axis.
     * @param y The components position on the Y axis.
     * @param mouseX The mouse's position on the X axis.
     * @param mouseY The mouse's position on the Y axis.
     * @return If the mouse is hovering over this component.
     */
    public boolean isHovering(double x, double y, int mouseX, int mouseY) {
        double maxX = x + getWidth();
        double maxY = y + getHeight();
        mouseX *= 2;
        mouseY *= 2;
        return x < mouseX && maxX > mouseX && y < mouseY && maxY > mouseY;
    }
}
