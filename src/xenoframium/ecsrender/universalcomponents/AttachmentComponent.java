package xenoframium.ecsrender.universalcomponents;

import xenoframium.ecs.Component;
import xenoframium.ecs.Entity;

/**
 * Created by chrisjung on 1/10/17.
 */
public class AttachmentComponent implements Component {
    public Entity parent;

    public AttachmentComponent(Entity e) {
        parent = e;
    }
}
