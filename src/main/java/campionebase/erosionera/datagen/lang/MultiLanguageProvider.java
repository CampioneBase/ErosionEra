package campionebase.erosionera.datagen.lang;

import campionebase.erosionera.datagen.DataProviderUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.KeyMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * 通用翻译文件生成器。
 *
 * <p>本身不绑定任何模组：模组 id 由构造参数传入，核心模组与附属模组共用这一个实现。
 * 一个生成器只对应一个模组，输出命名空间与输入来源命名空间必然是同一个
 */
public class MultiLanguageProvider extends LanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger(MultiLanguageProvider.class);
    public static final String TIMESTAMP_KEY = "_auto_gen_timestamp";

    /** 本生成器负责的模组 id，同时是输出命名空间与翻译来源的扫描范围 */
    private final String modId;
    private final String locale;
    /** 本次生成过程中失败的翻译来源，全部处理完后统一输出 */
    private final List<TranslationFailure> failures = new ArrayList<>();

    /**
     * @param output 数据生成输出
     * @param modId  目标模组 id：决定语言文件的输出路径/命名空间，同时限定翻译来源的扫描范围
     * @param locale 语言代码，如 {@code zh_cn}
     */
    public MultiLanguageProvider(PackOutput output, String modId, String locale) {
        super(output, modId, locale);
        this.modId = modId;
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        DataProviderUtils.scanClassesWithAnnotationByModid(Translation.class, this.modId)
            .forEach(this::addTranslationsByAnnotation);
        this.add(TIMESTAMP_KEY, new Date().toString());
        this.logFailures();
    }

    /**
     * 收集指定模组下所有 {@link Translation.Key} 中声明的翻译语言
     */
    public static Set<String> collectLanguages(String modId) {
        Set<String> languages = new TreeSet<>();
        DataProviderUtils.scanClassesWithAnnotationByModid(Translation.class, modId).forEach(clazz -> {
            for (Field field : clazz.getDeclaredFields()) {
                for (Translation.Key key : field.getAnnotationsByType(Translation.Key.class)) {
                    languages.add(key.lang());
                }
            }
        });
        return languages;
    }

    private void logFailures() {
        if (this.failures.isEmpty()) return;
        for (TranslationFailure failure : this.failures) {
            LOGGER.error("Failed to generate translation from source: {}", failure.source(), failure.cause());
        }
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
        for (Field field : clazz.getDeclaredFields()){
            for (Translation.Key key : field.getAnnotationsByType(Translation.Key.class)){
                if (!key.lang().equals(this.locale)) continue;
                String prefix = key.prefix().isEmpty() ? translation.prefix() : key.prefix();
                String[] suffixes = key.suffix().length == 0 ? translation.suffix() : key.suffix();
                try {
                    this.processField(field, prefix, suffixes, key.value());
                } catch (Exception e) {
                    this.failures.add(new TranslationFailure(clazz.getName() + "#" + field.getName(), e));
                }
            }
        }
    }

    // 解析单个字段，保留对各类型的支持
    private void processField(Field field, String prefix, String[] suffixes, String[] values) throws Exception {
        field.setAccessible(true);
        Object fieldValue = field.get(null);
        // 字符串键
        if (fieldValue instanceof String str){
            this.add(str, values[0]);
            return;
        }
        // 按键绑定
        if (fieldValue instanceof KeyMapping keyMapping){
            this.add(keyMapping.getName(), values[0]);
            return;
        }
        // 解析注册名
        ResourceLocation id = null;
        // 注册机
        if (fieldValue instanceof RegistryObject<?> registryObject){
            id = registryObject.getId();
        }
        // 成就
        if (fieldValue instanceof Advancement advancement){
            id = advancement.getId();
        }
        // id
        if (fieldValue instanceof ResourceLocation resourceLocation){
            id = resourceLocation;
        }

        // 使用字段构建
        if (id == null){
            String path = field.getName().toLowerCase(Locale.ROOT);
            id = ResourceLocation.fromNamespaceAndPath(this.modId, path);
        }

        String key = prefix.isEmpty() ? id.toLanguageKey() : id.toLanguageKey(prefix);
        this.addTranslation(key.replace('/', '.'), suffixes, values);
    }

    private record TranslationFailure(String source, Exception cause) {}
}
