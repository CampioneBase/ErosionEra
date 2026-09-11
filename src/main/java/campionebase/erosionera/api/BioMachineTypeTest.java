package campionebase.erosionera.api;

import javax.annotation.Nullable;

public interface BioMachineTypeTest<B, M extends B>{
    static <B, M extends B> BioMachineTypeTest<B, M> forClass(final Class<M> clazz) {
        return new BioMachineTypeTest<>() {
            @SuppressWarnings("unchecked")
            @Override
            public M tryCast(B machine) {
                return clazz.isInstance(machine) ? (M) machine : null;
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return clazz;
            }
        };
    }

    @Nullable
    M tryCast(B machine);

    Class<? extends B> getBaseClass();
}
