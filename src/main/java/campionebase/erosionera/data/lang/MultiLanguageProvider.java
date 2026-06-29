package campionebase.erosionera.data.lang;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.DataProviderUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.KeyMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

public class MultiLanguageProvider extends LanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger(MultiLanguageProvider.class);
    public static final String TIMESTAMP_KEY = "_auto_gen_timestamp";

    private final String locale;

    public MultiLanguageProvider(PackOutput output, String locale) {
        super(output, ErosionEra.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        DataProviderUtils.scanClassesWithAnnotation(Translation.class)
            .forEach(this::addTranslationsByAnnotation);
        this.add(TIMESTAMP_KEY, new Date().toString());
    }

    private void addTranslation(String key, String[] suffixes, String[] values){
        if (suffixes == null || values == null) return;
        for (int i = 0; i < suffixes.length; i++) {
            if (i < values.length){
                String suffix = suffixes[i];
                String k = suffix.isEmpty() ? key : key + "." + suffix.toLowerCase(Locale.ROOT);
                String v = values[i];
                if(v.isEmpty()){
                    LOGGER.error("Translation value is empty with key: {}", k);
                    continue;
                }
                this.add(k, v);
            }
            else break;
        }
    }

    // 读取翻译注解
    private void addTranslationsByAnnotation(Class<?> clazz){
        Translation translation = clazz.getAnnotation(Translation.class);
        if (translation == null) return;
        Translation.Language lang = translation.lang();
        if (!lang.locale.equals(this.locale)) return;
        String namespace = translation.namespace();
        String[] suffixes = translation.suffix();
        // 获取 ElementType.Field
        for (Field field : clazz.getDeclaredFields()){
            Annotation langAnnotation = field.getAnnotation(lang.clazz);
            if (langAnnotation == null) continue;
            // 获取翻译值
            String[] values;
            try {
                Method method = lang.clazz.getMethod("value");
                values = (String[]) method.invoke(langAnnotation);
            }
            catch (Exception e) {
                // 无法获取翻译值
                LOGGER.error(e);
                continue;
            }
            // 获取注册名
            ResourceLocation id = null;
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(null);
                // 字段
                if (fieldValue instanceof String str){
                    this.add(str, values[0]);
                    continue;
                }
                // 注册机
                if (fieldValue instanceof RegistryObject<?> registryObject){
                    id = registryObject.getId();
                }
                // 成就
                if (fieldValue instanceof Advancement advancement){
                    id = advancement.getId();
                }
                // 按键绑定
                if (fieldValue instanceof KeyMapping keyMapping){
                    this.add(keyMapping.getName(), values[0]);
                    continue;
                }
                // id
                if (fieldValue instanceof ResourceLocation resourceLocation){
                    id = resourceLocation;
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }

            // 使用字段构建
            if (id == null){
                String path = field.getName().toLowerCase(Locale.ROOT);
                id = ResourceLocation.fromNamespaceAndPath(namespace, path);
            }

            String key = translation.key().isEmpty() ? id.toLanguageKey() : id.toLanguageKey(translation.key());
            this.addTranslation(key.replace('/', '.'), suffixes, values);
        }
    }
}
