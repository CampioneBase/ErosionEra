package campionebase.erosionera.api;

import campionebase.erosionera.ErosionEra;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class BioMachineType<M extends IBioMachine>
        implements BioMachineTypeTest<IBioMachine, M>, Comparable<BioMachineType<M>> {
    public static final String KEY = "bio_machine_type";
    @NotNull
    private final ResourceLocation id;

    @NotNull
    private final Class<M> machineClazz;

    private final int orderWeight;

    private BioMachineType(Builder<M> builder) {
        this.id = builder.id;
        this.machineClazz = builder.machineClazz;
        this.orderWeight = builder.orderWeight;
    }

    public @NotNull ResourceLocation getId(){
        return this.id;
    }

    public String getDescriptionId(){
        return this.id.toLanguageKey(KEY);
    }

    public int getOrderWeight() {
        return this.orderWeight;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public M tryCast(IBioMachine machine) {
        return this.machineClazz.isInstance(machine) ? (M) machine : null;
    }

    @Override
    public Class<M> getBaseClass() {
        return this.machineClazz;
    }

    public static class Builder<M extends IBioMachine>{
        private final Class<M> machineClazz;
        private ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, "unknown");
        private int orderWeight = Integer.MIN_VALUE;

        public static <M extends IBioMachine> Builder<M> of(Class<M> clazz){ return new Builder<>(clazz); }

        public Builder(Class<M> clazz) { this.machineClazz = clazz; }
        public Builder<M> setOrderWeight(int orderWeight){ this.orderWeight = orderWeight; return this; }
        private BioMachineType<M> build() { return new BioMachineType<>(this); }
        public BioMachineType<M> build(String name){
            return this.build(ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name));
        }
        public BioMachineType<M> build(ResourceLocation id){
            this.id = id;
            return this.build();
        }
    }
    public static <M extends IBioMachine> Builder<M> builder(Class<M> clazz){ return Builder.of(clazz); }

    @Override
    public int compareTo(@NotNull BioMachineType<M> other) {
        if (this.orderWeight != other.orderWeight){
            return this.orderWeight - other.orderWeight;
        }
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BioMachineType<?> that)) return false;
        return id.equals(that.id) && machineClazz.equals(that.machineClazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, machineClazz);
    }
}
