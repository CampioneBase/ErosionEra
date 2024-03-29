package lunastic.erosion_era.util.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Supplier;

public interface DataCompound {

    /** 获取数据标签 */
    String getDataTag();

    /**
     * <h3>向nbt写入数据</h3>
     * 注意：此处 NbtCompound 已经是通过数据标签生成的实例对象
     * @see PlayerExtraData#writeNbt(NbtCompound)
     */
    void writeNbt(NbtCompound nbt);

    /**
     * <h3>从nbt读取数据</h3>
     * 注意：此处 NbtCompound 已经是通过数据标签生成的实例对象
     * @see PlayerExtraData#readNbt(NbtCompound)
     */
    void readNbt(NbtCompound nbt);


    /**
     * 玩家的tick事件(Mixin末尾插入)
     * @see PlayerEntity#tick()
     * @param player 该数据对象所关联的玩家实体
     */
    default void tick(PlayerEntity player){};


    /**
     * 添加新的数据结构
     * @param supplier 添加数据对象生成方法
     */
    static void register(Supplier<? extends DataCompound> supplier){
        PlayerExtraData.register(supplier);
    }
}
