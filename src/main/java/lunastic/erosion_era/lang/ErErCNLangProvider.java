package lunastic.erosion_era.lang;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.init.ErErDamageSources;
import lunastic.erosion_era.init.ErErStatusEffects;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.damage.DamageSource;

import java.nio.file.Path;

public class ErErCNLangProvider extends FabricLanguageProvider {
    // Language Tag
    public static final String LANG = "zh_cn";

    public ErErCNLangProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator, LANG);
        ErosionEraMod.LOGGER.info("Loading Language " + LANG + "...");
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {

        this.effectTranslation(builder);
        this.damageTranslation(builder);

        try {
            Path langFilePath = dataGenerator.getModContainer().findPath(
                    "assets/"+ ErosionEraMod.ID +"/lang/"+ LANG +".json"
            ).get();
            builder.add(langFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    public void effectTranslation(TranslationBuilder builder){
        builder.add(ErErStatusEffects.EROSION_EFFECT, "侵蚀");
    }

    public void damageTranslation(TranslationBuilder builder){

        builder.add(getDamageSourceTranslationKey(ErErDamageSources.EROSION), "%1$s 饱受侵蚀折磨");
        builder.add(getDamageSourceTranslationKey(ErErDamageSources.EROSION, TKTag._PLAYER), "%1$s 接受 %1$s 的侵蚀");
    }

    // 简化 DamageSource 后缀编写
    private static String getDamageSourceTranslationKey(DamageSource damageSource, TKTag... tags){
        StringBuilder s = new StringBuilder(TKTag.DEATH_ATTACK_ + damageSource.getName());
        for (TKTag tag : tags){ if (tag.isSuffix) s.append(tag.key); }
        return s.toString();
    }

    // 可用 Translation Key
    private enum TKTag{
        DEATH_ATTACK_("death.attack.", false),
        _ITEM(".item", true),
        _PLAYER(".player", true);
        private final String key;
        private final boolean isSuffix;
        TKTag(String key, boolean isSuffix){ this.key = key; this.isSuffix = isSuffix; }
    }
}
