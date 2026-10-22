package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.spell.behaviour.RuneBehaviour;
import com.mojang.serialization.MapCodec;

public final class RuneType<T extends RuneBehaviour> {
    public static final String KEY = "rune_type";
    private final RuneCategory category;
    private final MapCodec<T> codec;
    public RuneType(RuneCategory category, MapCodec<T> codec){
        this.category = category;
        this.codec = codec;
    }

    public MapCodec<T> codec() {
        return this.codec;
    }
}
