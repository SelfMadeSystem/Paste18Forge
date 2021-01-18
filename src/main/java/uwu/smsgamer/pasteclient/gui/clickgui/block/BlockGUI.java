package uwu.smsgamer.pasteclient.gui.clickgui.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.Nullable;
import uwu.smsgamer.pasteclient.values.NumberValue;

import java.util.*;

public abstract class BlockGUI {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final int spacing = 10;
    public static final NumberValue WIDTH_ADD = new NumberValue("WidthAdd", "Adding to the component width", 2, -10, 10, 1, NumberValue.NumberType.INTEGER);
    public static final NumberValue childOffset = new NumberValue("ComponentOffset", "Offset for each component", 80, 0, 500, 10, NumberValue.NumberType.INTEGER);
    public static final NumberValue animationTime = new NumberValue("AnimationTime", "Time for the animation", 250, 0, 1000, 50, NumberValue.NumberType.INTEGER);
    @Nullable
    public BlockGUI child = null;
    public BlockGUI pastChild = null;
    public BlockGUI parent = null;
    public List<BlockComponent> components = new ArrayList<>();
    @Nullable
    public BlockComponent activeComponent = null;
    public int spawnTime;
    public int despawnTime;
    public boolean despawned;

    public BlockGUI() {
    }

    public BlockGUI(BlockGUI parent) {
        this.parent = parent;
    }

    public void setChild(BlockGUI child) {
        if (child == null && this.child != null) {
            this.pastChild = this.child;
            this.pastChild.despawned = true;
            this.pastChild.despawnTime = animationTime.getInt();
        }
        this.child = child;
    }

    public double getChildCount() {
        double d = 0;
        BlockGUI child = this.child;
        while (child != null) {
            d += (double) child.spawnTime / animationTime.getInt();
            if (child.pastChild != null) d += (double) child.pastChild.despawnTime / animationTime.getInt();
            child = child.child;
        }
        if (pastChild != null) d += (double) pastChild.despawnTime / animationTime.getInt();
        return d;
    }

    public int getMidScreenX() {
        ScaledResolution res = new ScaledResolution(mc);
        return res.getScaledWidth();
    }

    public int getMidScreenY() {
        ScaledResolution res = new ScaledResolution(mc);
        return res.getScaledHeight();
    }

    protected int cachedWidth;
    protected int cachedHeight;
    protected boolean sizeCached;

    public int getHeight() {
        if (sizeCached) return cachedHeight;
        cachedWidth = BlockComponent.WIDTH.getInt() + WIDTH_ADD.getInt();
        int stop = getMidScreenY() * 2 - BlockComponent.HEIGHT.getInt() * 4;
        int height0 = spacing;
        int height = spacing;
        for (BlockComponent component : components) {
            height += component.getHeight() + spacing;
            if (height > stop) {
                cachedWidth += BlockComponent.WIDTH.getInt() + WIDTH_ADD.getInt();
                height0 = Math.max(height0, height);
                height = spacing;
            }
        }
        return cachedHeight = Math.max(height0, height);
    }

    public int getWidth() {
        if (!sizeCached) getHeight();
        return cachedWidth;
    }

    public void render(int mouseX, int mouseY, int timeDiff) {
        sizeCached = false;
        if (despawned) {
            if (despawnTime > 0) despawnTime -= timeDiff;
            if (despawnTime - timeDiff < 0) {
                parent.pastChild = null;
                return;
            }
            BlockClickGUI.renderer.setOpacity((double) despawnTime / animationTime.getInt());
        } else {
            if (spawnTime < animationTime.getInt()) spawnTime += timeDiff;
            if (spawnTime > animationTime.getInt()) spawnTime = animationTime.getInt();
            BlockClickGUI.renderer.setOpacity((double) spawnTime / animationTime.getInt());
        }
        int xOffset = (int) (childOffset.getInt() * getChildCount());
        BlockClickGUI.renderer.drawRect(0, 0, getMidScreenX() * 2, getMidScreenY() * 2, BlockClickGUI.GUI_BACKGROUND.getColor());
        BlockClickGUI.renderer.drawRect(getMidScreenX() - getWidth() / 2F - spacing - xOffset, getMidScreenY() - getHeight() / 2F + spacing,
          getWidth() + spacing * 2, getHeight(), BlockClickGUI.GUI_COLOR.getColor());
        BlockClickGUI.renderer.drawOutline(getMidScreenX() - getWidth() / 2F - spacing - xOffset, getMidScreenY() - getHeight() / 2F + spacing,
          getWidth() + spacing * 2, getHeight(), 2, BlockClickGUI.GUI_BORDER.getColor());
        List<BlockComponent> cmpts = new ArrayList<>(components);
        final int startY = getMidScreenY() - (getHeight() - spacing * 2) / 2 - cmpts.get(0).getHeight()/2;
        final int stopY = getMidScreenY() + (getHeight() + spacing * 2) / 2;
        int currentX = -getWidth() / 2 + BlockComponent.WIDTH.getInt() / 2 + WIDTH_ADD.getInt() / 2;
        int currentY = startY;
        for (BlockComponent component : cmpts) {
            currentY += component.getHeight() + spacing;
            if (currentY > stopY) {
                currentY = startY + component.getHeight() + spacing;
                currentX += BlockComponent.WIDTH.getInt() + WIDTH_ADD.getInt();
            }
            component.render(getMidScreenX() - xOffset + currentX, currentY, mouseX, mouseY);
        }
        if (despawned) {
            BlockClickGUI.renderer.setOpacity(1);
            return;
        }
        if (child != null) {
            child.render(mouseX, mouseY, timeDiff);
        }
        if (pastChild != null) {
            pastChild.render(mouseX, mouseY, timeDiff);
        }
        BlockClickGUI.renderer.setOpacity(1);
    }

    public void click(int mouseX, int mouseY, int mouseButton, int pressType) {
        if (child == null) {
            List<BlockComponent> cmpts = new ArrayList<>(components);
            for (BlockComponent component : cmpts) {
                component.click(mouseX, mouseY, mouseButton, pressType);
            }
        } else {
            child.click(mouseX, mouseY, mouseButton, pressType);
        }
    }

    public void keyPress(char typedChar, int keyCode, int pressType) {
        if (child == null) {
            List<BlockComponent> cmpts = new ArrayList<>(components);
            for (BlockComponent component : cmpts) {
                component.keyPress(typedChar, keyCode, pressType);
            }
        } else {
            child.keyPress(typedChar, keyCode, pressType);
        }
    }

    public abstract void reload();

    public void close() {
        this.parent.reload();
        this.parent.setChild(null);
    }
}
