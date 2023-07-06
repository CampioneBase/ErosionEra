package lunastic.erosion_era.data;

import com.google.common.collect.Maps;
import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.mixin.entity.PlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * <h3>玩家数据池</h3>
 * <p>
 *     用于存储玩家对象，提供数据管理接口
 * </p>
 * @author Lunastic
 * @see PlayerEntityMixin#readErosionNbt(NbtCompound, CallbackInfo)
 * @see PlayerEntityMixin#writeErosionNbt(NbtCompound, CallbackInfo)
 */
public final class PlayerData {
    public static final String NBT_TAG = "EE_PlayerData";

    // todo 将 key 改写为 uuid:String
    // 目前使用 PlayerEntity 是为了方便玩家数据测试
    private static final Map<PlayerEntity, PlayerData> POOL = Maps.newHashMap();

    private PlayerData() { this.erosionData = ErosionData.create(); }

    public static PlayerData create(){
        return new PlayerData();
    }

    @Nullable
    public static PlayerData get(PlayerEntity player){
        return POOL.get(player);
    }

    public static PlayerData getOrCreate(PlayerEntity player){
        return POOL.computeIfAbsent(player, k -> create());
    }

    public static void put(PlayerEntity player, PlayerData data){
        POOL.put(player, data);
    }

    /* ---- 数据分项 ---- */
    public ErosionData erosionData;
    /* ---------------- */

    // test
    public void tick(){
        erosionData.tick();
    }

    public void writeNbt(NbtCompound nbt){
        NbtCompound nbtCompound = new NbtCompound();
        erosionData.writeNbt(nbtCompound);
        nbt.put(NBT_TAG, nbtCompound);

        // 打桩检测
        POOL.forEach((p, d) -> ErosionEraMod.LOGGER.info(
                "[TEST] {} Data: Erosion - {} (Level {})",
                p.getName().getString(),
                d.erosionData.getErosion(),
                d.erosionData.getErosionLevel()
        ));
    }

    public void readNbt(NbtCompound nbt){
        if(nbt.contains(NBT_TAG,NbtElement.COMPOUND_TYPE)){
            NbtCompound nbtCompound = nbt.getCompound(NBT_TAG);
            erosionData.readNbt(nbtCompound);
        }
    }
}
