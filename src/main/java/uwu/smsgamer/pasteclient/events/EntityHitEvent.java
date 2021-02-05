package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.entity.Entity;

public class EntityHitEvent extends EventCancellable {
    private final EventType eventType;
    public Entity target;

    public EntityHitEvent(Entity target, EventType eventType) {
        this.target = target;
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
