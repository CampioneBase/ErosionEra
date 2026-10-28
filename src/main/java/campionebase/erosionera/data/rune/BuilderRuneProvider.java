package campionebase.erosionera.data.rune;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.spell.RuneEntry;
import campionebase.erosionera.magic.spell.behaviour.SimpleBuilder;
import campionebase.erosionera.registry.RuneTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class BuilderRuneProvider extends RuneEntryProvider{

    public BuilderRuneProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, ErosionEra.MODID, helper);
    }

    @Override
    protected void addRunes(BiConsumer<String, RuneEntry> consumer) {
        factory(RuneTypes.BUILDER_SIMPLE)
                .builder("test1")
                .behaviour(new SimpleBuilder())
                .mana(0)
                .delay(0)
                .recipe()
                    .core(Items.DIAMOND_BLOCK)
                    .material(Items.AMETHYST_SHARD)
                    .material(Items.GLASS)
                    .material(Items.AMETHYST_SHARD)
                    .material(Items.GLASS)
                    .mpt(5)
                    .tick(100)
                .endRecipe()
                .save(consumer)

                .builder("test2")
                .behaviour(new SimpleBuilder())
                .mana(1)
                .delay(100)
                .recipe()
                    .core(Items.IRON_BLOCK)
                    .material(Tags.Items.GEMS)
                    .material(Tags.Items.BONES)
                    .material(Items.AMETHYST_SHARD)
                    .material(Items.AMETHYST_SHARD)
                    .mpt(5)
                    .tick(100)
                .endRecipe()
                .save(consumer)
                ;

    }

    @Override
    public @NotNull String getName() {
        return super.getName() + ":Builder";
    }
}
