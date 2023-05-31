package lunastic.erosion_era.recipe;

import com.google.common.collect.Lists;
import lunastic.erosion_era.init.ErErBlockItems;
import lunastic.erosion_era.init.ErErGroups;
import lunastic.erosion_era.init.ErErItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

import java.util.List;
import java.util.function.Consumer;

public class ErErRecipeProvider extends FabricRecipeProvider {
    public ErErRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    public static final List<ItemConvertible> EXAMPLE = Util.make(Lists.newArrayList(), list -> {
        list.add(ErErBlockItems.SHIMMER_PILLAR);
        list.add(ErErBlockItems.SHIMMER_CORE);
    });

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

        RecipeProviders.crossRecipe(exporter, ErErItems.EROSION_DEBRIS, Items.IRON_HELMET, ErErItems.BASIC_HELMET);

    }
}
