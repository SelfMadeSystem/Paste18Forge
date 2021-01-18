package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;

public class MouseMoveEvent implements Event {
    public final EventType eventType;
    public final int x, y;
    public final int trueX, trueY;

    public MouseMoveEvent(EventType eventType, int x, int y) {
        this(eventType, x, y, x, y);
    }

    public MouseMoveEvent(EventType eventType, int x, int y, int trueX, int trueY) {
        this.eventType = eventType;
        this.x = x;
        this.y = y;
        this.trueX = trueX;
        this.trueY = trueY;
    }
}
