package lunastic.erosion_era.world.data;

import com.google.common.collect.Sets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * <h3>玩家数据池</h3>
 * <p>
 *     用于存储玩家对象
 * </p>
 */
public final class PlayerData {
    public static final String NBT_TAG = "EE_PlayerData";

    // 弱数据池
    private static final Map<PlayerEntity, PlayerData> POOL = new WeakHashMap<>();

    public PlayerData() { this.erosionData = ErosionData.create(); }

    public static PlayerData create(){
        return new PlayerData();
    }

    public static PlayerData get(PlayerEntity player){
        return POOL.get(player);
    }

    public static void put(PlayerEntity player, PlayerData data){
        POOL.put(player, data);
    }

    public ErosionData erosionData;

    public void tick(){
        erosionData.tick();
    }

    public void writeNbt(NbtCompound nbt){
        NbtCompound nbtCompound = new NbtCompound();
        erosionData.writeNbt(nbtCompound);
        nbt.put(NBT_TAG, nbtCompound);
    }

    public void readNbt(NbtCompound nbt){
        if(nbt.contains(NBT_TAG,NbtElement.COMPOUND_TYPE)){
            NbtCompound nbtCompound = nbt.getCompound(NBT_TAG);
            erosionData.readNbt(nbtCompound);
        }
    }
}
