package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;

public class UpdateEvent implements Event {
    private final EventType eventType;

    public UpdateEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
