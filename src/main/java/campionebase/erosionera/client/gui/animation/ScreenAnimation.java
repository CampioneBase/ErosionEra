package campionebase.erosionera.client.gui.animation;

public abstract class ScreenAnimation {
    protected boolean started;
    protected boolean finished;

    public final boolean advance(float dt){
        if (this.finished) return true;
        if (!this.started) {
            this.started = true;
            this.onStart();
        }
        if (this.update(dt)){
            this.finished = true;
            this.onFinish();
        }
        return this.finished;
    }

    protected abstract boolean update(float dt);

    protected void onStart() {}
    protected void onFinish() {}

    public void cancel(){
        this.finished = true;
    }
    public boolean isFinished(){
        return this.finished;
    }
}
