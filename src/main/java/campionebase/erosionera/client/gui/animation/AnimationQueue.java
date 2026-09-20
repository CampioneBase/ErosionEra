package campionebase.erosionera.client.gui.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AnimationQueue<T extends ScreenAnimation & Renderable> {

    private final Queue<T> pending = new LinkedList<>();
    private final List<T> active = new ArrayList<>();
    // 放行间隔
    private final float interval;
    // 放行最大数量
    private final int maxActive;

    private float spawnCooldown;

    public AnimationQueue(float interval, int maxActive){
        this.interval = interval;
        this.maxActive = maxActive;
    }

    public void enqueue(T animation){
        this.pending.add(animation);
    }

    public void tick(float dt){
        // 活跃区推进
        for (int i = active.size() - 1; i >= 0; i--){
            if (this.active.get(i).advance(dt)) active.remove(i);
        }

        this.spawnCooldown -= dt;
        // 等候区 =》 活跃区
        while (this.spawnCooldown <= 0 && !this.pending.isEmpty() && this.active.size() < this.maxActive){
            T animation = this.pending.remove();
            this.active.add(animation);
            animation.advance(0.0F);
            this.spawnCooldown += this.interval;
        }

        if (this.spawnCooldown < 0) this.spawnCooldown = 0;
    }

    public void render(GuiGraphics g, int mx, int my, float pt) {
        for (T a : this.active) a.render(g, mx, my, pt);
    }
}
