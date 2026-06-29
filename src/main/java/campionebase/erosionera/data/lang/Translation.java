package campionebase.erosionera.data.lang;

import campionebase.erosionera.ErosionEra;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Translation {
    /** 翻译键 */
    String key() default "";
    /** 模组名 */
    String namespace() default ErosionEra.MODID;
    /** 物品的翻译名称格式 */
    String[] suffix() default {""};
    /** 翻译语言 */
    Language lang() default Language.ZH_CN;

    enum Language {
        ZH_CN("zh_cn", Translation.ZH_CN.class),
        EN_US("en_us", Translation.EN_US.class);

        public final String locale;
        public final Class<? extends Annotation> clazz;
        Language(String locale, Class<? extends Annotation> clazz) {
            this.locale = locale;
            this.clazz = clazz;
        }
    }

    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.FIELD})
    @interface ZH_CN { String[] value();}
    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.FIELD})
    @interface EN_US { String[] value();}
}
