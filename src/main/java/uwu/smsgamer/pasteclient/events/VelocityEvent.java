package uwu.smsgamer.pasteclient.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.Entity;

public class VelocityEvent extends EventCancellable {
    public Entity entity;
    public double x, y, z;

    public VelocityEvent(Entity entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
