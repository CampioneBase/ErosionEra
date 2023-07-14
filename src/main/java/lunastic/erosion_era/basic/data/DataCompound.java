package lunastic.erosion_era.basic.data;

import net.minecraft.nbt.NbtCompound;

public interface DataCompound {

    String getDataTag();
    void writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);

    default void tick(){};
}
