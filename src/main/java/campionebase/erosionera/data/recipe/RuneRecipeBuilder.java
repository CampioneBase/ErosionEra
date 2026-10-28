package campionebase.erosionera.data.recipe;

import campionebase.erosionera.recipe.RuneRecipe;
import campionebase.erosionera.registry.ErErRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
//noinspection unused
public class RuneRecipeBuilder {
    // 符文的 ID
    private final ResourceLocation result;
    // 配方的 ID
    private final ResourceLocation id;
    private Ingredient core;
    private final List<Ingredient> materials = new ArrayList<>();
    private int mpt = 0;
    private int tick = 0;

    protected RuneRecipeBuilder(String namespace, String path) {
        this.result = ResourceLocation.fromNamespaceAndPath(namespace, path);
        this.id = ResourceLocation.fromNamespaceAndPath(namespace, RuneRecipe.NAME + "/" + path);
    }

    public static RuneRecipeBuilder of(String namespace, String path) {
        return new RuneRecipeBuilder(namespace, path);
    }

    public static RuneRecipeBuilder of(@NotNull ResourceLocation runeId){
        Objects.requireNonNull(runeId, "Rune id is null");
        return new RuneRecipeBuilder(runeId.getNamespace(), runeId.getPath());
    }

    // 构造前必须校验
    public static void check(RuneRecipeBuilder builder) throws IllegalStateException {
        if (builder.id == null) throw new IllegalStateException("RuneRecipe id is not set. result=" + builder.result.toString());
        if (builder.core == null) throw new IllegalStateException("RuneRecipe core is not set. id=" + builder.id);
        if (builder.materials.isEmpty()) throw new IllegalStateException("RuneRecipe materials is empty. id=" + builder.id);
        if (builder.mpt < 0) throw new IllegalStateException("RuneRecipe mpt is negative. id=" + builder.id);
        if (builder.tick < 0) throw new IllegalStateException("RuneRecipe tick is negative. id=" + builder.id);
    }

    public RuneRecipeBuilder core(RegistryObject<Item> registry) { this.core = Ingredient.of(registry.get()); return this; }
    public RuneRecipeBuilder core(ItemLike item) { this.core = Ingredient.of(item); return this; }
    public RuneRecipeBuilder core(TagKey<Item> tag) { this.core = Ingredient.of(tag); return this; }
    public RuneRecipeBuilder core(Ingredient ingredient) { this.core = ingredient; return this; }

    public RuneRecipeBuilder material(RegistryObject<Item> registry) { this.materials.add(Ingredient.of(registry.get())); return this; }
    public RuneRecipeBuilder material(ItemLike item) { this.materials.add(Ingredient.of(item)); return this; }
    public RuneRecipeBuilder material(TagKey<Item> tag) { this.materials.add(Ingredient.of(tag)); return this; }
    public RuneRecipeBuilder material(Ingredient ingredient) { this.materials.add(ingredient); return this; }
    public RuneRecipeBuilder materials(Ingredient... ingredients) { this.materials.addAll(Arrays.asList(ingredients)); return this; }
    public RuneRecipeBuilder materials(Collection<Ingredient> ingredients) { this.materials.addAll(ingredients); return this; }

    public RuneRecipeBuilder mpt(int mpt) { this.mpt = mpt; return this; }
    public RuneRecipeBuilder tick(int tick) { this.tick = tick; return this; }

    public RuneRecipe build() {
        check(this);
        return new RuneRecipe(id, core, List.copyOf(materials), mpt, tick, result);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        check(this);
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                json.add("core", core.toJson());

                JsonArray materialsJson = new JsonArray();
                for (Ingredient ingredient : materials) {
                    materialsJson.add(ingredient.toJson());
                }
                json.add("materials", materialsJson);

                json.addProperty("mpt", mpt);
                json.addProperty("tick", tick);

                json.addProperty("result", result.toString());
            }

            @Override
            public @NotNull ResourceLocation getId() {
                return id;
            }

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                return ErErRecipes.RUNE_CRAFT_SERIALIZER.get();
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }
}
