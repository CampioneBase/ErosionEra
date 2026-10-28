package campionebase.erosionera.data.rune;

import campionebase.erosionera.data.recipe.RuneRecipeBuilder;
import campionebase.erosionera.magic.spell.RuneEntry;
import campionebase.erosionera.magic.spell.RuneType;
import campionebase.erosionera.magic.spell.behaviour.RuneBehaviour;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"UnusedReturnValue", "unused", "SameParameterValue"})
public abstract class RuneEntryProvider implements DataProvider {
    private static final Set<String> names = new HashSet<>();
    private final PackOutput output;
    protected final String modid;
    private final Recipe runeRecipes;

    public RuneEntryProvider(PackOutput output, String modid, ExistingFileHelper helper) {
        this.output = output;
        this.modid = modid;
        this.runeRecipes = new Recipe(output);
    }

    protected abstract void addRunes(BiConsumer<String, RuneEntry> consumer);

    @Override
    public final @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path root = this.output.getOutputFolder();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        this.addRunes((name, rune) -> {
            if (!names.add(name)){
                throw new IllegalStateException("Duplicate rune entry. id=" + this.modid + ":" + name);
            }
            JsonElement json = RuneEntry.codec()
                    .encodeStart(JsonOps.INSTANCE, rune)
                    .getOrThrow(false, msg -> {
                        throw new IllegalStateException("Failed to encode rune. " +
                                "id=" + this.modid + ":" + name +
                                ", msg=" + msg
                        );
                    });

            Path file = root.resolve("data")
                    .resolve(this.modid)
                    .resolve(RuneEntry.DIR)
                    .resolve(name + ".json");

            futures.add(DataProvider.saveStable(cache, json, file));
        });
        futures.add(this.runeRecipes.run(cache));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Rune Provider";
    }

    /**
     *  新建一个指定符文类型的符文工厂，使用类的构造器里的 modid 作为 namespace
     */
    protected <T extends RuneBehaviour> Factory<T> factory(RuneType<T> type) {
        return new Factory<>(type, this.runeRecipes, this.modid);
    }

    /**
     *  新建一个指定符文类型的符文工厂，使用类的构造器里的 modid 作为 namespace
     */
    protected <T extends RuneBehaviour> Factory<T> factory(Supplier<RuneType<T>> supplier){
        return factory(supplier.get());
    }

    protected static class Factory<T extends RuneBehaviour> {
        private final RuneType<T> type;
        private final Recipe recipeProvider;
        private final String modid;

        private Factory(RuneType<T> type, Recipe recipeProvider, String modid) {
            this.type = type;
            this.recipeProvider = recipeProvider;
            this.modid = modid;
        }

        /** 新建一个指定符文类型的符文构造器 */
        protected Builder<T> builder(String name){
            return new Builder<>(this, name);
        }
    }

    protected static class Builder<T extends RuneBehaviour> {
        private final Factory<T> owner;
        private final String name;
        private T behaviour;
        private int mana = 0;
        private int delay = 0;

        private Builder(Factory<T> owner, String name) {
            this.owner = owner;
            this.name = name;
        }

        protected Builder<T> behaviour(@NotNull T behaviour){
            this.behaviour = behaviour;
            return this;
        }

        protected Builder<T> mana(int mana){

            this.mana = mana;
            return this;
        }

        protected Builder<T> delay(int delay){
            this.delay = delay;
            return this;
        }

        /**
         *  该符文的配方生成器
         *  <br>与 {@link RecipeBuilder#endRecipe()} 成对出现
         */
        protected RecipeBuilder<T> recipe(){
            return new RecipeBuilder<>(this.owner.modid, this.name, this);
        }

        protected RuneEntry build(){
            Objects.requireNonNull(this.behaviour, "behaviour is null");
            if (this.mana < 0) throw new IllegalArgumentException("mana is negative");
            if (this.delay < 0) throw new IllegalArgumentException("delay is negative");
            return new RuneEntry(this.owner.type, this.behaviour, this.mana, this.delay);
        }

        /** 将符文保存到执行区，并返回到符文工厂 */
        protected Factory<T> save(BiConsumer<String, RuneEntry> consumer){
            consumer.accept(this.name, this.build());
            return this.owner;
        }
    }

    private static class Recipe extends RecipeProvider {
        private final List<FinishedRecipe> recipes = new ArrayList<>();

        private Recipe(PackOutput output) {
            super(output);
        }

        @Override
        protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
            this.recipes.forEach(consumer);
        }
    }


    // 配方生成器代理
    protected static class RecipeBuilder<T extends RuneBehaviour> {
        private final Builder<T> callback;
        private final RuneRecipeBuilder recipeBuilder;

        private RecipeBuilder(String namespace, String path, Builder<T> callback) {
            this(RuneRecipeBuilder.of(namespace, path), callback);
        }

        private RecipeBuilder(RuneRecipeBuilder recipeBuilder, Builder<T> callback){
            this.callback = callback;
            this.recipeBuilder = recipeBuilder;
        }

        public RecipeBuilder<T> core(RegistryObject<Item> registry) {
            this.recipeBuilder.core(registry);
            return this;
        }

        public RecipeBuilder<T> core(ItemLike item) {
            this.recipeBuilder.core(item);
            return this;
        }

        public RecipeBuilder<T> core(TagKey<Item> tag) {
            this.recipeBuilder.core(tag);
            return this;
        }

        public RecipeBuilder<T> core(Ingredient ingredient) {
            this.recipeBuilder.core(ingredient);
            return this;
        }

        public RecipeBuilder<T> material(RegistryObject<Item> registry) {
            this.recipeBuilder.material(registry);
            return this;
        }

        public RecipeBuilder<T> material(ItemLike item) {
            this.recipeBuilder.material(item);
            return this;
        }

        public RecipeBuilder<T> material(TagKey<Item> tag) {
            this.recipeBuilder.material(tag);
            return this;
        }

        public RecipeBuilder<T> material(Ingredient ingredient) {
            this.recipeBuilder.material(ingredient);
            return this;
        }

        public RecipeBuilder<T> materials(Ingredient... ingredients) {
            this.recipeBuilder.materials(ingredients);
            return this;
        }

        public RecipeBuilder<T> materials(Collection<Ingredient> ingredients) {
            this.recipeBuilder.materials(ingredients);
            return this;
        }

        public RecipeBuilder<T> mpt(int mpt) {
            this.recipeBuilder.mpt(mpt);
            return this;
        }

        public RecipeBuilder<T> tick(int tick) {
            this.recipeBuilder.tick(tick);
            return this;
        }

        /**
         * 返回符文生成器，并将数据提交给内部的配方生成器
         * <br>与 {@link Builder#recipe()} 成对出现 */
        protected Builder<T> endRecipe() {
            this.recipeBuilder.save(this.callback.owner.recipeProvider.recipes::add);
            return this.callback;
        }
    }
}
