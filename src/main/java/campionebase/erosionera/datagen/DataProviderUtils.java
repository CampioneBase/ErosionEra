package campionebase.erosionera.datagen;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据生成期的类扫描工具。
 *
 * <p>不绑定任何模组：按「模组 id + 注解类型」扫描，一个模组只会扫到自己 jar 内的
 * 标注类。扫描范围即模组本身，因此不再需要注解上的 namespace 属性来区分归属。
 */
public class DataProviderUtils {

    /**
     * 扫描指定模组内、包含指定注解的类。
     *
     * <p>只读取该模组自身 {@code ModFileScanData} 中的注解，附属模组不会互相污染，
     * 也不会扫到核心模组或其它模组的内容。
     *
     * @param clazz 注解类型
     * @param modid 目标模组 id
     * @return 被注解的类集合
     * @throws IllegalStateException 目标模组未加载时抛出（通常是 modid 拼写错误）
     */
    public static Set<Class<?>> scanClassesWithAnnotationByModid(Class<? extends Annotation> clazz, String modid) {
        IModFileInfo modFile = ModList.get().getModFileById(modid);
        if (modFile == null) {
            throw new IllegalStateException("Cannot scan annotated classes: mod not loaded: " + modid);
        }
        Type annotationType = Type.getType(clazz);
        return modFile.getFile()
                .getScanResult()
                .getAnnotations()
                .stream()
                .filter(annotationData -> annotationType.equals(annotationData.annotationType()))
                .map(DataProviderUtils::loadClass)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static Class<?> loadClass(ModFileScanData.AnnotationData annotationData) {
        try {
            return Class.forName(annotationData.clazz().getClassName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
