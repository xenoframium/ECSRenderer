package xenoframium.ecsrender.components;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.RenderStrategy;

public class RenderComponent3D implements Component, AutoCloseable {
    public final RenderStrategy strategy;
    public final boolean hasTransparency;

    public RenderComponent3D(RenderStrategy strategy, boolean hasTransparency) {
        this.strategy = strategy;
        this.hasTransparency = hasTransparency;
    }

    @Override
    public void close() {
        strategy.close();
    }
}
