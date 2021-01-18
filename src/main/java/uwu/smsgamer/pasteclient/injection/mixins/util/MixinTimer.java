package uwu.smsgamer.pasteclient.injection.mixins.util;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.*;
import uwu.smsgamer.pasteclient.injection.interfaces.IMixinTimer;

@Mixin(Timer.class)
public class MixinTimer implements IMixinTimer {
    @Shadow
    public float timerSpeed;

    @Override
    public float getTimerSpeed() {
        return this.timerSpeed;
    }

    @Override
    public void setTimerSpeed(float timerSpeed) {
        this.timerSpeed = timerSpeed;
    }
}
