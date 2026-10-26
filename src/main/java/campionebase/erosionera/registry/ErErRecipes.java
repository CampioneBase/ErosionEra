package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.recipe.RuneRecipe;
import campionebase.erosionera.recipe.RuneRecipeSerializer;
import campionebase.erosionera.recipe.custom.MagicStoneBindingRecipe;
import campionebase.erosionera.recipe.custom.MagicStoneUnbindingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 配方相关注册。
 *
 * <p><b>不要用 {@code RecipeType.register(String)}</b>：那是原版已废弃的静态注册，
 * 会把 id 注册到 {@code minecraft:} 命名空间下，且在静态初始化期写注册表。
 * 这里统一走 {@link DeferredRegister}。
 */
public class ErErRecipes {

    /** 炼金/符文台这一类的配方类别。 */
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ErosionEra.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, ErosionEra.MODID);

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        SERIALIZERS.register(bus);
    }

    // ------------------------------------------------------------------ 符文台

    /** 符文台配方类别：{@code erosionera:rune_table}。 */
    public static final RegistryObject<RecipeType<RuneRecipe>> RUNE_TABLE_TYPE =
            RECIPE_TYPES.register("rune_table", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return ErosionEra.MODID + ":rune_table";
                }
            });

    /** 符文台配方序列化器：JSON 里的 {@code "type": "erosionera:rune_table"}。 */
    public static final RegistryObject<RecipeSerializer<RuneRecipe>> RUNE_TABLE_SERIALIZER =
            SERIALIZERS.register("rune_table", RuneRecipeSerializer::new);

    // ------------------------------------------------------------------ 装载 / 卸下（合成台特例配方）

    public static final RegistryObject<SimpleCraftingRecipeSerializer<MagicStoneBindingRecipe>> SPELL_BINDING =
            SERIALIZERS.register("spell_binding", () -> new SimpleCraftingRecipeSerializer<>(
                    MagicStoneBindingRecipe::new
            ));

    public static final RegistryObject<SimpleCraftingRecipeSerializer<MagicStoneUnbindingRecipe>> SPELL_UNBINDING =
            SERIALIZERS.register("spell_unbinding", () -> new SimpleCraftingRecipeSerializer<>(
                    MagicStoneUnbindingRecipe::new
            ));
}
