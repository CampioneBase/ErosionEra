package campionebase.erosionera.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public record BioMachinePatch(
        @NotNull CompoundTag values,
        @NotNull List<String> removals
) {
    public static final BioMachinePatch EMPTY = new BioMachinePatch(new CompoundTag(), List.of());

    public static BioMachinePatch full(@NotNull CompoundTag data) {
        return new BioMachinePatch(data.copy(), List.of());
    }

    public static BioMachinePatch diff(@NotNull CompoundTag oldData, @NotNull CompoundTag newData) {
        CompoundTag values = new CompoundTag();
        TreeSet<String> removals = new TreeSet<>();

        for (String key : newData.getAllKeys()) {
            if (!oldData.contains(key) || !Objects.equals(oldData.get(key), newData.get(key))) {
                values.put(key, newData.get(key).copy());
            }
        }
        for (String key : oldData.getAllKeys()) {
            if (!newData.contains(key)) {
                removals.add(key);
            }
        }
        return new BioMachinePatch(values, List.copyOf(removals));
    }

    public static void save(@NotNull FriendlyByteBuf buf, BioMachinePatch patch) {
        buf.writeNbt(patch.values());
        buf.writeCollection(patch.removals(), FriendlyByteBuf::writeUtf);
    }

    public static BioMachinePatch load(@NotNull FriendlyByteBuf buf) {
        CompoundTag values = buf.readNbt();
        List<String> removals = buf.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
        return new BioMachinePatch(values == null ? new CompoundTag() : values, List.copyOf(removals));
    }

    public @NotNull CompoundTag apply(@NotNull CompoundTag current) {
        CompoundTag result = current.copy();
        for (String removal : this.removals) {
            result.remove(removal);
        }
        result.merge(this.values.copy());
        return result;
    }

    public boolean isEmpty() {
        return this.values.isEmpty() && this.removals.isEmpty();
    }
}
