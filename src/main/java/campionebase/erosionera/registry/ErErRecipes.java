package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.spell.SpellBindingRecipe;
import campionebase.erosionera.magic.spell.SpellUnbindingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ErErRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(
            ForgeRegistries.Keys.RECIPE_SERIALIZERS, ErosionEra.MODID
    );

    public static final RegistryObject<SimpleCraftingRecipeSerializer<SpellBindingRecipe>> SPELL_BINDING =
            RECIPES.register("spell_binding", () -> new SimpleCraftingRecipeSerializer<>(
                    SpellBindingRecipe::new
            ));

    public static final RegistryObject<SimpleCraftingRecipeSerializer<SpellUnbindingRecipe>> SPELL_UNBINDING =
            RECIPES.register("spell_unbinding", () -> new SimpleCraftingRecipeSerializer<>(
                    SpellUnbindingRecipe::new
            ));
}
