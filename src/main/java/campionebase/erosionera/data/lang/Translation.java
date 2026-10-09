package campionebase.erosionera.data.lang;

import campionebase.erosionera.ErosionEra;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Translation {
    /** 翻译键前缀 */
    String prefix() default "";
    /** 模组名 */
    String namespace() default ErosionEra.MODID;
    /** 翻译键后缀 */
    String[] suffix() default {""};

    /**
     * 字段翻译注解，通过 lang 指定翻译语言。
     * prefix / suffix 可覆盖 Translation 上的同名设置。
     * 可重复使用，以在同一个字段上提供多种语言的翻译。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @Repeatable(Keys.class)
    @interface Key {
        /** 翻译值 */
        String[] value();
        /** 翻译语言 */
        String lang() default Translation.DEFAULT_LANG;
        /** 覆盖 Translation 的翻译键前缀，为 null 时沿用 Translation 的设置 */
        String prefix() default "";
        /** 覆盖 Translation 的翻译键后缀，为 null 时沿用 Translation 的设置 */
        String[] suffix() default {};
    }

    /** Translation.Key 的容器注解，支持在字段上重复使用 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Keys {
        Key[] value();
    }

    /** 默认翻译语言 */
    String DEFAULT_LANG = "zh_cn";
}
