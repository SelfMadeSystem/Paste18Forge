package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.*;

public class StrafeEvent implements Cancellable, Event {
    public float strafe, forward, friction;
    private boolean cancelled;

    public StrafeEvent(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }
}
