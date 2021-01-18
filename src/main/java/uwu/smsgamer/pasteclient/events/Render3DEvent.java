package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.Event;

public class Render3DEvent implements Event {
    public final float partialTicks;
    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
