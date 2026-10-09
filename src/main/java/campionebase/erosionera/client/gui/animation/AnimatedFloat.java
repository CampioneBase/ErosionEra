package campionebase.erosionera.client.gui.animation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class AnimatedFloat extends ScreenAnimation{
    private float value, start, target;
    private final float totalSeconds;
    @NotNull
    private final Easing easing;

    private float elapsed;

    public AnimatedFloat(float init, float totalSeconds) {
        this(init, totalSeconds, x -> x);
    }

    public AnimatedFloat(float init, float totalSeconds, @NotNull Easing easing){
        Objects.requireNonNull(easing);
        this.totalSeconds = Math.max(totalSeconds, 0.0001F);
        this.easing = easing;
        this.snapTo(init);
    }

    @Override
    protected boolean update(float dt) {
        this.elapsed += Math.max(0.0F, dt);
        float progress = this.totalSeconds <= 0.0F
                ? 1.0F
                : Math.min(this.elapsed / this.totalSeconds, 1.0F);

        float eased = progress <= 0.0F ? 0.0F : this.easing.apply(progress);
        this.value = this.start + (this.target - this.start) * eased;

        if (progress >= 1.0F) {
            this.value = this.target;
            return true;
        }
        return false;
    }

    public void setTarget(float target){
        if (this.target == target) return;
        this.target = target;
        this.elapsed = 0.0F;
        this.started = false;
        this.finished = false;
    }

    public void snapTo(float value){
        this.value = this.start = this.target = value;
        this.elapsed = this.totalSeconds;
        this.started = false;
        this.finished = false;
    }

    public float value(){
        return this.value;
    }
}
