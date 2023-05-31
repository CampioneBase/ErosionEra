package lunastic.erosion_era.lang;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.nio.file.Path;

public class ErErCNLangProvider extends FabricLanguageProvider {
    public static final String LANG = "zh_cn";

    public ErErCNLangProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator, LANG);
        ErosionEraMod.LOGGER.info("Loading Language " + LANG + "...");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {



        try {
            Path existingFilePath = dataGenerator.getModContainer().findPath(
                    "assets/"+ ErosionEraMod.ID +"/lang/"+ LANG +".json"
            ).get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
