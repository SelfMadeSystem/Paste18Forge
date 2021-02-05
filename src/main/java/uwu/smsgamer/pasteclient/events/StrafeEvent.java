package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class StrafeEvent extends EventCancellable {
    public float strafe, forward, friction;

    public StrafeEvent(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }
}
