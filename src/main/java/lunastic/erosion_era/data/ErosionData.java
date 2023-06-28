package lunastic.erosion_era.data;

import net.minecraft.nbt.NbtCompound;

public class ErosionData{

    // 到下一级所需要的积蓄值(索引意义为当前等级)
    private static final int[] NEXT_LEVEL = {1000, 1250, 1500, 1750, 2000};

    public static final int COLOR = 0xd412e0;
    public static final int A_COLOR = 0xffd412e0;


    // 侵蚀度
    public int erosion = 0;
    // 侵蚀等级
    public int erosionLevel = 0;

    private ErosionData(){}

    public static ErosionData create(){ return new ErosionData(); }
    public static ErosionData of(int erosion, int erosionLevel){
        ErosionData data = new ErosionData();
        data.erosion = erosion;
        data.erosionLevel = erosionLevel;
        return data;
    }

    /** 获取到达下一等级所需经验 */
    public int getNextLevelErosion(){
        return this.isMaxLevel() ? 0 : NEXT_LEVEL[this.erosionLevel];
    }

    /** 是否为最大等级 */
    public boolean isMaxLevel(){
        return this.erosionLevel >= NEXT_LEVEL.length;
    }

    // test
    public void tick() {
        erosion ++;
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
