package lunastic.erosion_era.basic.data;

import com.google.common.collect.Maps;
import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.basic.mixin.entity.PlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.include.com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <h3>玩家数据池</h3>
 * <p>
 *     用于存储玩家对象，提供数据管理接口
 * </p>
 * @author Lunastic
 */
public final class PlayerExtraData {
    public static final Logger LOGGER = LogManager.getLogger("L.P.E.D");

    private static final String NBT_TAG = "Lunastic_PlayerExtraData";

    // 数据类型
    private static final Set<Supplier<? extends DataCompound>> DATA_INIT = Sets.newHashSet();

    /** 添加新的数据结构 */
    public static void register(Supplier<? extends DataCompound> supplier){
        DATA_INIT.add(supplier);
    }

    // todo 将 key 改写为 uuid:String
    // 目前使用 PlayerEntity 是为了方便玩家数据测试
    private static final Map<PlayerEntity, PlayerExtraData> POOL = Collections.synchronizedMap(Maps.newIdentityHashMap());

    public static PlayerExtraData newData(){ return new PlayerExtraData(); }

    public static @NotNull PlayerExtraData get(@NotNull PlayerEntity player){
        return POOL.computeIfAbsent(player, p -> {
            LOGGER.info("{}({}) possibly lost data!",
                    p.getName().getString(),
                    p.getUuidAsString()
            );
            return newData();
        });
    }

    public static PlayerExtraData put(@NotNull PlayerEntity player, PlayerExtraData data){
        return POOL.put(player, data);
    }

    private final Map<String, DataCompound> data = Collections.synchronizedMap(Maps.newIdentityHashMap());

    private PlayerExtraData() {
        // 初始化数据树
        DATA_INIT.forEach(supplier -> {
            DataCompound instance = supplier.get();
            this.data.put(instance.getDataTag(), instance);
        });
    }

    // 获取数据对象
    @NotNull
    public <T extends DataCompound> T get(@NotNull String tag, @NotNull Class<T> clazz){
        return clazz.cast(data.get(tag));
    }

    // test
    public void tick(){
        data.values().forEach(DataCompound::tick);
    }

    public void writeNbt(NbtCompound nbt){
        NbtCompound nbtCompound = new NbtCompound();
        this.data.values().forEach(value -> value.writeNbt(nbtCompound));
        nbt.put(NBT_TAG, nbtCompound);
    }

    public void readNbt(NbtCompound nbt){
        if(nbt.contains(NBT_TAG, NbtElement.COMPOUND_TYPE)){
            NbtCompound nbtCompound = nbt.getCompound(NBT_TAG);
            data.values().forEach(value -> value.readNbt(nbtCompound));
        }
    }
}
