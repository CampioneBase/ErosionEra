package lunastic.erosion_era.lang;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.init.ErErDamageSources;
import lunastic.erosion_era.init.ErErRules;
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
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {

        this.effectTranslations(builder);
        this.damageTranslations(builder);
        this.ruleTranslations(builder);

        try {
            Path langFilePath = dataGenerator.getModContainer().findPath(
                    "assets/"+ ErosionEraMod.ID +"/lang/"+ LANG +".json"
            ).get();
            builder.add(langFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    // 药水效果翻译
    private void effectTranslations(TranslationBuilder builder){
        builder.add(ErErStatusEffects.EROSION_EFFECT, "侵蚀");
    }

    // 伤害说明翻译
    private void damageTranslations(TranslationBuilder builder){

        builder.add(getDSTK(ErErDamageSources.EROSION), "%1$s 饱受侵蚀折磨");
        builder.add(getDSTK(ErErDamageSources.EROSION, TKTag._PLAYER), "%1$s 接受 %1$s 的侵蚀");
    }

    // 规则翻译
    private void ruleTranslations(TranslationBuilder builder){
        builder.add(ErErRules.EROSION_SELECTOR.getTranslationKey(), "仅原版侵蚀");
    }

    // 简化 DamageSource 后缀编写
    // Damage Source Translation Key
    private static String getDSTK(DamageSource damageSource, TKTag... tags){
        StringBuilder s = new StringBuilder(TKTag.DEATH_ATTACK_.key + damageSource.getName());
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
