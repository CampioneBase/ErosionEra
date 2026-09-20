package campionebase.erosionera.client.gui.animation;

import net.minecraft.client.gui.components.Renderable;

public abstract class TimedAnimation extends ScreenAnimation implements Renderable {
    protected float elapsed = 0.0f;
    @Override
    protected final boolean update(float dt) {
        this.elapsed += dt;
        float total = this.total();
        if (total <= 0.0F) {
            this.apply(0.0F, 1.0F);
            return true;
        }
        if (this.elapsed >= total) {
            this.apply(total, 1.0F);
            return true;
        }
        this.apply(this.elapsed, this.elapsed / total);
        return false;
    }

    protected abstract float total();

    protected abstract void apply(float elapsed, float normalized);
}
