package lunastic.erosion_era;

import lunastic.erosion_era.achievement.AdvancementsProvider;
import lunastic.erosion_era.lang.ErErCNLangProvider;
import lunastic.erosion_era.recipe.ErErRecipeProvider;
import lunastic.erosion_era.tag.ErErBlockTagGenerator;
import lunastic.erosion_era.tag.ErErItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {

        generator.addProvider(ErErCNLangProvider::new);
        generator.addProvider(AdvancementsProvider::new);
        generator.addProvider(ErErRecipeProvider::new);
        generator.addProvider(ErErBlockTagGenerator::new);
        generator.addProvider(ErErItemTagGenerator::new);
    }

}
