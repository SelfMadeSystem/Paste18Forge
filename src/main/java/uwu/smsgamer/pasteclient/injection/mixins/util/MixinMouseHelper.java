package uwu.smsgamer.pasteclient.injection.mixins.util;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.*;
import uwu.smsgamer.pasteclient.events.MouseMoveEvent;
import uwu.smsgamer.pasteclient.injection.interfaces.IMixinMouseHelper;

@Mixin(MouseHelper.class)
public class MixinMouseHelper implements IMixinMouseHelper {
    @Shadow
    public int deltaX, deltaY;
    public int mode;
    public int sideX, sideY;
    public int x, y;
    public int addX, addY;
    public double multX, divX, multY, divY;

    /**
     * @author Sms_Gamer_3808
     */
    @Overwrite
    public void mouseXYChange() {
        final int dX = Mouse.getDX();
        final int dY = Mouse.getDY();
        EventManager.call(new MouseMoveEvent(EventType.PRE, dX, dY));
        switch (mode) {
            case 0:
                deltaX = dX;
                deltaY = dY;
                break;
            case 1:
                deltaX = x;
                deltaY = y;
                break;
            case 2:
                deltaX = dX + addX;
                deltaY = dY + addY;
                break;
            case 3:
                int nX = dX;
                int nY = dY;

                if (sideX != 0) {
                    if (dX > 0 == sideX > 0) nX *= multX;
                    else nX /= divX;
                }
                if (sideY != 0) {
                    if (dY > 0 == sideY > 0) nY *= multY;
                    else nY /= divY;
                }

                deltaX = nX;
                deltaY = nY;
        }
        EventManager.call(new MouseMoveEvent(EventType.POST, deltaX, deltaY, dX, dY));
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void reset() {
        this.multX = divX = multY = divY = mode = x = y = addX = addY = 0;
    }

    @Override
    public void setSideX(int side) {
        this.sideX = side;
    }

    @Override
    public void setSideY(int side) {
        this.sideY = side;
    }

    @Override
    public void setX(int amount) {
        x = amount;
    }

    @Override
    public void setY(int amount) {
        y = amount;
    }

    @Override
    public void setAddX(int amount) {
        addX = amount;
    }

    @Override
    public void setAddY(int amount) {
        addY = amount;
    }

    @Override
    public void setMultX(double amount) {
        multX = amount;
    }

    @Override
    public void setDivX(double amount) {
        divX = amount;
    }

    @Override
    public void setMultY(double amount) {
        multY = amount;
    }

    @Override
    public void setDivY(double amount) {
        divY = amount;
    }
}
