package lunastic.erosion_era.util.data;

import com.google.common.collect.Maps;
import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.include.com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <h3>玩家数据池</h3>
 * <p>
 *     用于存储玩家对象，提供数据管理接口
 * </p>
 * @author Lunastic
 * @version 0.1
 */
public final class PlayerExtraData implements DataCompound{
    public static final Logger LOGGER = LogManager.getLogger("L.P.E.D");

    // 数据标签
    private static final String NBT_TAG = "Lunastic_PlayerExtraData";

    // 数据类型
    private static final Set<Supplier<? extends DataCompound>> DATA_INIT = Sets.newHashSet();

    /** 添加新的数据结构 */
    protected static void register(Supplier<? extends DataCompound> supplier){
        DATA_INIT.add(supplier);
    }

    private static final Map<String, PlayerExtraData> POOL = Collections.synchronizedMap(Maps.newIdentityHashMap());

    public static PlayerExtraData newData(){ return new PlayerExtraData(); }

    public static @NotNull PlayerExtraData get(@NotNull PlayerEntity player){
        return POOL.computeIfAbsent(player.getUuidAsString(), s -> {
            LOGGER.info("{}({}) possibly lost data!",
                    player.getName().getString(),
                    player.getUuidAsString()
            );
            return newData();
        });
    }

    public static PlayerExtraData put(@NotNull PlayerEntity player, @NotNull PlayerExtraData data){
        return PlayerExtraData.put(player.getUuidAsString(), data);
    }

    public static PlayerExtraData put(@NotNull String uuid, @NotNull PlayerExtraData data){
        return POOL.put(uuid, data);
    }

    // 数据对象集
    private final Map<String, DataCompound> data = Collections.synchronizedMap(Maps.newIdentityHashMap());

    private PlayerExtraData() {
        // 初始化数据树
        DATA_INIT.forEach(supplier -> {
            DataCompound instance = supplier.get();
            this.data.put(instance.getDataTag(), instance);
        });
    }

    /**
     * 从玩家数据中获取数据对象
     * @param tag 数据对象标签
     * @param clazz 数据对象类型
     * @return 数据对象
     * @exception ClassCastException 存储（注册）的数据类型对象不匹配
     */
    @NotNull
    public <T extends DataCompound> T get(@NotNull String tag, @NotNull Class<T> clazz){
        return clazz.cast(this.data.get(tag));
    }

    /**
     * 从玩家数据种获取数据对象
     * @param tag 数据对象标签
     * @return 数据对象
     */
    @NotNull
    public DataCompound get(@NotNull String tag){
        return this.data.get(tag);
    }

    // 玩家实体的tick事件
    public void tick(PlayerEntity player){
        this.data.values().forEach(dataCompound -> dataCompound.tick(player));
    }

    @Override
    public String getDataTag() {
        return NBT_TAG;
    }

    public void writeNbt(NbtCompound nbt){
        NbtCompound nbtTotal = new NbtCompound();
        this.data.forEach((key, value) -> {
            NbtCompound nbtCompound = new NbtCompound();
            value.writeNbt(nbtCompound);
            nbtTotal.put(key, nbtCompound);
        });
        nbt.put(NBT_TAG, nbtTotal);
    }

    public void readNbt(NbtCompound nbt){
        if(nbt.contains(NBT_TAG, NbtElement.COMPOUND_TYPE)){
            NbtCompound nbtTotal = nbt.getCompound(NBT_TAG);
            this.data.forEach((key, value) -> {
                if(nbtTotal.contains(key, NbtElement.COMPOUND_TYPE))
                    value.readNbt(nbtTotal.getCompound(key));
            });
        }
    }
}
