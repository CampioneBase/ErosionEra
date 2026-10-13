package campionebase.erosionera.data;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据生成期的类扫描工具。
 *
 * <p>不绑定任何模组：既可按注解类型扫描（跨模组），也可进一步按注解上的
 * namespace 属性过滤，从而让每个模组只生成属于自己的那部分内容。
 */
public class DataProviderUtils {
    /** 注解中表示「命名空间 / 模组 id」的属性名 */
    private static final String NAMESPACE_ATTRIBUTE = "namespace";

    /**
     * 扫描包含指定注解的类（不限命名空间）
     */
    public static Set<Class<?>> scanClassesWithAnnotation(Class<? extends Annotation> clazz) {
        Type annotationType = Type.getType(clazz);
        return ModList.get().getAllScanData().stream()
                .flatMap(scanData -> scanData.getAnnotations().stream())
                .filter(annotationData -> annotationType.equals(annotationData.annotationType()))
                .map(DataProviderUtils::loadClass)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 扫描包含指定注解、且注解 namespace 属性等于给定值的类。
     * 用于「一个模组只处理自己的翻译来源」。
     */
    public static Set<Class<?>> scanClassesWithAnnotation(Class<? extends Annotation> clazz, String namespace) {
        Type annotationType = Type.getType(clazz);
        return ModList.get().getAllScanData().stream()
                .flatMap(scanData -> scanData.getAnnotations().stream())
                .filter(annotationData -> annotationType.equals(annotationData.annotationType()))
                .filter(annotationData -> namespace.equals(readNamespace(annotationData.annotationData())))
                .map(DataProviderUtils::loadClass)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 从注解数据里读取 namespace 属性。
     * Forge 的扫描数据把注解属性放在 Map 里，字符串常量即 String；
     * 若以数组形式给出则取单元素。
     */
    private static String readNamespace(Map<String, Object> annotationData) {
        if (annotationData == null) return null;
        Object value = annotationData.get(NAMESPACE_ATTRIBUTE);
        if (value instanceof String str) {
            return str;
        }
        if (value instanceof List<?> list && list.size() == 1 && list.get(0) instanceof String str) {
            return str;
        }
        return null;
    }

    private static Class<?> loadClass(ModFileScanData.AnnotationData annotationData) {
        try {
            return Class.forName(annotationData.clazz().getClassName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
