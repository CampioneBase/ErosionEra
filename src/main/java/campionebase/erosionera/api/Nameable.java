package campionebase.erosionera.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Nameable {
    default String getDefaultName() {
        return "Unnamed";
    }

    void setName(String name);
    @Nullable
    String getName();
}
