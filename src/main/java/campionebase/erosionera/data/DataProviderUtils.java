package campionebase.erosionera.data;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DataProviderUtils {
    /**
     * 扫描包含指定注解的类
     */
    public static Set<Class<?>> scanClassesWithAnnotation(Class<? extends Annotation> clazz) {
        return ModList.get().getAllScanData().stream()
                .flatMap(scanData -> scanData.getAnnotations().stream())
                .filter(annotationData ->
                        Type.getType(clazz).equals(annotationData.annotationType()))
                .map(annotationData -> {
                    try {
                        return Class.forName(annotationData.clazz().getClassName());
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
