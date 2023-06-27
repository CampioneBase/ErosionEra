package lunastic.erosion_era.world.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class ErosionData{

    public int getErosion() {
        return erosion;
    }

    public void setErosion(int erosion) {
        this.erosion = erosion;
    }

    public int getErosionLevel() {
        return erosionLevel;
    }

    public void setErosionLevel(int erosionLevel) {
        this.erosionLevel = erosionLevel;
    }

    // 侵蚀度
    private int erosion = 0;
    // 侵蚀等级
    private int erosionLevel = 0;

    private ErosionData(){}

    public static ErosionData create(){ return new ErosionData(); }
    public static ErosionData of(int erosion, int erosionLevel){
        ErosionData data = new ErosionData();
        data.erosion = erosion;
        data.erosionLevel = erosionLevel;
        return data;
    }

    public void tick() {
        erosion += 2;
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putInt("erosion", this.erosion);
        nbt.putInt("erosionLevel", this.erosionLevel);
    }

    public void readNbt(NbtCompound nbt){
        this.erosion = nbt.getInt("erosion");
        this.erosionLevel = nbt.getInt("erosionLevel");
    }
}
