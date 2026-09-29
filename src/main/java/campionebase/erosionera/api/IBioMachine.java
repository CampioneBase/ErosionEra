package campionebase.erosionera.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IBioMachine extends Nameable {
    String TAG_LOADED = "is_loaded";
    String TAG_ID = "id";
    String TAG_NAME = "CustomName";

    BlockPos getBlockPos();
    @NotNull
    BioMachineType<? extends IBioMachine> getMachineType();
    @NotNull
    default ResourceLocation getId(){
        return this.getMachineType().getId();
    }

    default boolean equals(IBioMachine machine){
        if (machine == null) return false;
        return this.getBlockPos().equals(machine.getBlockPos()) &&
                this.getMachineType().equals(machine.getMachineType());
    }

    default String getDefaultName() {
        return Component.translatable(this.getMachineType().getDescriptionId())
                .append(Component.literal("[" + this.getBlockPos().toShortString() + "]"))
                .getString();
    }

    default CompoundTag getCustomData(){
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_ID, this.getId().toString());
        if (this.getName() != null){
            tag.putString(TAG_NAME, this.getName());
        }
        return tag;
    }

    static String getDisplayName(CompoundTag customData) {
        if (customData.contains(TAG_NAME, Tag.TAG_STRING)) {
            String name = customData.getString(TAG_NAME);
            if (!name.isEmpty()) {
                return name;
            }
        }

        String id = customData.getString(TAG_ID);
        return id.isEmpty() ? "Unnamed" : id;
    }
}
