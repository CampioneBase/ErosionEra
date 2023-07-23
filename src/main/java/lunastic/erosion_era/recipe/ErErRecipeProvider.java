package lunastic.erosion_era.recipe;

import com.google.common.collect.Lists;
import lunastic.erosion_era.init.BlockItems;
import lunastic.erosion_era.init.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Util;

import java.util.List;
import java.util.function.Consumer;

public class ErErRecipeProvider extends FabricRecipeProvider {
    public ErErRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    public static final List<ItemConvertible> EXAMPLE = Util.make(Lists.newArrayList(), list -> {
        list.add(BlockItems.SHIMMER_PILLAR);
        list.add(BlockItems.SHIMMER_CORE);
    });

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

        RecipeProviders.crossRecipe(exporter, Items.EROSION_DEBRIS, net.minecraft.item.Items.IRON_HELMET, Items.BASIC_HELMET);

    }
}
