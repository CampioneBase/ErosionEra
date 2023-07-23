package lunastic.erosion_era.recipe;

import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;

import java.util.function.Consumer;

public final class RecipeProviders {

    public static void crossRecipe(
            Consumer<RecipeJsonProvider> exporter,
            ItemConvertible cross,
            ItemConvertible core,
            ItemConvertible output
    ){
        ShapedRecipeJsonBuilder.create(output)
                .input('$', cross)
                .input('#', core)
                .pattern(" $ ").pattern("$#$").pattern(" $ ")
                .group("cross_recipe")
                .criterion("demo", RecipeProvider.conditionsFromItem(core))
                .offerTo(exporter);
    }
}
