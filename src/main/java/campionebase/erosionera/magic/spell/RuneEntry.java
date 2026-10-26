package campionebase.erosionera.magic.spell;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.spell.behaviour.RuneBehaviour;
import campionebase.erosionera.registry.RuneTypes;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record RuneEntry(
    RuneType<?> type,
    RuneBehaviour behaviour,
    int mana,
    int delay
) {
    /** 数据包目录：`data/<namespace>/rune/*.json`。 */
    public static final String DIR = "rune";
    private static Map<ResourceLocation, RuneEntry> entries = Map.of();
    private static Codec<RuneEntry> _codec;
    public static Codec<RuneEntry> codec(){
        if (_codec == null) {
            _codec = RuneTypes.codec().dispatch("type",
                    RuneEntry::type,
                    type -> {
                        @SuppressWarnings("unchecked")
                        MapCodec<RuneBehaviour> behaviourCodec = (MapCodec<RuneBehaviour>) type.codec();
                        return RecordCodecBuilder.<RuneEntry>mapCodec(instance -> instance.group(
                                behaviourCodec.fieldOf("behaviour").forGetter(RuneEntry::behaviour),
                                Codec.intRange(0, 100).optionalFieldOf("mana", 0).forGetter(RuneEntry::mana),
                                Codec.intRange(0, 100).optionalFieldOf("delay", 0).forGetter(RuneEntry::delay)
                        ).apply(instance, (b, m, d) -> new RuneEntry(type, b, m, d))).codec();
                    }
            );
        }
        return _codec;
    }

    @Nullable
    public static RuneEntry get(ResourceLocation id){
        if (id == null) return null;
        return entries.get(id);
    }

    public static class Loader extends SimpleJsonResourceReloadListener {
        public Loader() {
            super(new Gson(), DIR);
        }

        @Override
        protected void apply(@NotNull Map<ResourceLocation, JsonElement> files,
                             @NotNull ResourceManager manager,
                             @NotNull ProfilerFiller filler)
        {
            Map<ResourceLocation, RuneEntry> map = new HashMap<>();
            files.forEach((id, json) -> RuneEntry.codec()
                    .parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(err ->
                            ErosionEra.LOGGER.error("Bad rune {}: {}", id, err)
                    )
                    .ifPresent(entry -> map.put(id, entry)));
            entries = Map.copyOf(map);
            ErosionEra.LOGGER.info("Loaded {} rune(s) from {}", map.size(), DIR);
        }
    }

    public static Set<ResourceLocation> idView() {
        return entries.keySet();
    }
}
