package campionebase.erosionera.client.gui.animation;

public final class AnimatedFloat extends ScreenAnimation{
    private float value, target;
    private final float secondsPerUnit;

    public AnimatedFloat(float init, float secondsPerUnit){
        this.value = this.target = init;
        this.secondsPerUnit = Math.max(secondsPerUnit, 0.0001F);
    }

    @Override
    protected boolean update(float dt) {
        return this.approach(dt / this.secondsPerUnit);
    }

    public void setTarget(float target){
        if (this.target == target) return;
        this.target = target;
        this.finished = false;
    }

    public void snapTo(float value){
        this.value = this.target = value;
        this.started = false;
        this.finished = false;
    }

    public float value(){
        return this.value;
    }

    private boolean approach(float amount){
        if (this.value < this.target) {
            this.value = Math.min(this.target, this.value + amount);
        } else if (this.value > this.target) {
            this.value = Math.max(this.target, this.value - amount);
        }
        return this.value == this.target;
    }
}
