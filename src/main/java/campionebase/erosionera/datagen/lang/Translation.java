package campionebase.erosionera.datagen.lang;

import java.lang.annotation.*;

/**
 * 标记「携带翻译来源」的类，用于数据生成阶段自动产出语言文件。
 *
 * <p>本注解与具体模组无关，也不再声明命名空间：翻译来源的归属由扫描时传入的
 * 模组 id 决定（只扫描该模组自身 jar 内的标注类），因此核心模组与任意附属模组
 * 都可以复用同一套注解，附属模组无需再持有同义类的副本。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Translation {
    /** 翻译键前缀 */
    String prefix() default "";
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
        /** 覆盖 Translation 的翻译键前缀，为空时沿用 Translation 的设置 */
        String prefix() default "";
        /** 覆盖 Translation 的翻译键后缀，为空时沿用 Translation 的设置 */
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
