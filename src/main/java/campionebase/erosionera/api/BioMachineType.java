package campionebase.erosionera.api;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.network.packet.BioControllerCommandPacket;
import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

public final class BioMachineType<M extends IBioMachine>
        implements BioMachineTypeTest<IBioMachine, M>, Comparable<BioMachineType<M>> {
    public static final String KEY = "bio_machine_type";
    @Deprecated
    public static final BioMachineType<IBioMachine> EMPTY = BioMachineType.Builder.of(IBioMachine.class).build();
    @NotNull
    private final ResourceLocation id;
    @NotNull
    private final Class<M> machineClazz;

    private BioMachineType(Builder<M> builder) {
        this.id = builder.id;
        this.machineClazz = builder.machineClazz;
    }

    public @NotNull ResourceLocation getId(){
        return this.id;
    }

    public String getDescriptionId(){
        return this.id.toLanguageKey(KEY);
    }

    public void handleCommand(BioControllerCommandPacket packet, IBioMachine machine, ServerLevel level, BlockPos corePos){

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

        public static <M extends IBioMachine> Builder<M> of(Class<M> clazz){ return new Builder<>(clazz); }

        public Builder(Class<M> clazz) { this.machineClazz = clazz; }
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
