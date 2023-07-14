package lunastic.erosion_era.basic.data;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import static lunastic.erosion_era.basic.data.PlayerExtraData.LOGGER;

/**
 * <h2>侵蚀数据</h2>
 * <p>
 *   存储和简单管理玩家的侵蚀数据
 * </p>
 *
 * <ul>属性：
 *     <li></li>
 * </ul>
 * <ul>API:
 *     <li> {@link #add(int)} </li>
 *     <li> {@link #subtract(int)} </li>
 *     <li> {@link #suppress(int)} </li>
 *     <li> {@link #clear()} </li>
 *     <li> {@link #clean()} </li>
 *     <li> {@link #getNextLevelErosion()} </li>
 *     <li> {@link #getTotalErosion()} </li>
 * </ul>
 * @author Lunastic
 * @see PlayerExtraData
 */
public final class ErosionData implements DataCompound{

    // 数据标签
    public static final String DATA_TAG = "Erosion";

    /**
     * 到下一级所需要的积蓄值(索引意义为当前等级)
     * <br>
     * 最大等级即为数组长度
     * */
    private static final int[] NEXT_LEVEL = {1000, 1250, 1500, 1750, 2000};

    // 控制台信息
    public static final String DATA_EXCEPTION = "erosion.data.exception";
    public static final String DATA_FIX = "erosion.data.fix";

    // 主色
    public static final int COLOR_RGB = 0xd412e0;
    public static final int COLOR_ARGB = COLOR_RGB + 0xff000000;
    public static final int COLOR_RGBA = COLOR_RGB * 0x100 + 0xff;

    // 侵蚀度
    private int erosion = 0;
    // 侵蚀等级
    private int erosionLevel = 0;

    public int getErosion() {
        return erosion;
    }

    public int getErosionLevel() {
        return erosionLevel;
    }

    private ErosionData(){}

    public static @NotNull ErosionData get(@NotNull PlayerEntity player){
        return PlayerExtraData.get(player).get(ErosionData.DATA_TAG, ErosionData.class);
    }

    public static ErosionData create(){ return new ErosionData(); }
    public static ErosionData of(int erosion, int erosionLevel){
        ErosionData data = new ErosionData();
        data.erosion = erosion;
        data.erosionLevel = erosionLevel;
        return data;
    }

    /** 获取到达下一等级所需侵蚀度 */
    public int getNextLevelErosion(){
        return this.isMaxLevel() ? 0 : NEXT_LEVEL[this.erosionLevel];
    }

    /** 获取总侵蚀值 */
    public int getTotalErosion(){
        if(this.isMaxLevel()) return Arrays.stream(NEXT_LEVEL).sum();
        int result = this.erosion;
        for (int i = 0; i < this.erosionLevel; i++) result += NEXT_LEVEL[i];
        return result;
    }

    /** 是否为最大等级 */
    public boolean isMaxLevel(){
        return this.erosionLevel >= NEXT_LEVEL.length;
    }

    /** 增加侵蚀值 */
    public void add(int value){
        this.add(value, true);
    }
    /** 增加侵蚀值 */
    public void add(int value, boolean levelChange){
        if(value == 0 || this.isMaxLevel()) return;
        if(value > 0)
            if(levelChange) this.addWithLevelUp(this.erosion + value);
            else this.erosion = Math.min(this.erosion + value, this.getNextLevelErosion() - 1);
        else this.subtract(-value);
    }

    // 拥有升级空间的增加
    private void addWithLevelUp(int value){
        while (value >= this.getNextLevelErosion()){
            value -= this.getNextLevelErosion();
            this.erosionLevel ++ ;
            // 达到最大等级 跳出循环并返回
            if(this.isMaxLevel()) {
                this.erosion = 0;
                return;
            }
        }
        this.erosion = value;
    }

    /** 减少侵蚀值 */
    public void subtract(int value){
        this.subtract(value, true);
    }
    /** 减少侵蚀值 */
    public void subtract(int value, boolean levelChange){
        if(value == 0) return;
        if(value > 0)
            if(levelChange) this.subtractWithLevelDown(this.erosion - value);
            else this.erosion = Math.max(this.erosion - value, 0);
        else this.add(-value);
    }

    private void subtractWithLevelDown(int value){
        while (value < 0){
            this.erosionLevel --;
            // 等级不够降低 则全为0 跳出循环并返回
            if (this.erosionLevel < 0){
                this.erosionLevel = 0;
                this.erosion = 0;
                return;
            }
            value += this.getNextLevelErosion();
        }
        this.erosion = value;
    }

    /** 抑制侵蚀值（不会影响等级） */
    public void suppress(int value){
        this.subtract(value, false);
    }

    /** 清除侵蚀度（保留侵蚀等级） */
    public void clean(){
        this.erosion = 0;
    }

    /** 完全清除侵蚀度 */
    public void clear(){
        this.erosion = 0;
        this.erosionLevel = 0;
    }

    /** 等级提升 */
    public void levelUp(ErosionChangeType type){
        this.erosionLevel = Math.min(this.erosionLevel + 1, NEXT_LEVEL.length);
        if(this.isMaxLevel()){
            this.erosion = 0;
            return;
        }
        switch (type){
            case PROGRESS -> this.erosion = this.erosion
                    * NEXT_LEVEL[this.erosionLevel]
                    / NEXT_LEVEL[this.erosionLevel - 1];
            case CLEAN -> this.erosion = 0;
            default -> {}
        }
    }

    /** 等级降低 */
    public void levelDown(ErosionChangeType type){
        if (this.isMaxLevel()) {
            this.erosionLevel = NEXT_LEVEL.length - 1;
            this.erosion = 0;
            return;
        }
        if (this.erosionLevel <= 0) return;
        this.erosionLevel = Math.min(this.erosionLevel - 1, NEXT_LEVEL.length);
        switch (type){
            case PROGRESS -> this.erosion = this.erosion
                    * NEXT_LEVEL[this.erosionLevel]
                    / NEXT_LEVEL[this.erosionLevel + 1];
            case CLEAN -> this.erosion = 0;
            case KEEP -> this.erosion = Math.min(this.getNextLevelErosion() - 1, this.erosion);
            default -> {}
        }
    }

    public boolean checkData(){
        return this.erosionLevel >= 0 && this.erosionLevel <= NEXT_LEVEL.length &&
            this.erosion >= 0 && this.erosion <= this.getNextLevelErosion();
    }

    public boolean checkAndFixData(){
        boolean check = true;
        if (this.erosionLevel < 0){
            this.erosionLevel = 0;
            check = false;
        }
        if (this.erosionLevel > NEXT_LEVEL.length){
            this.erosionLevel = NEXT_LEVEL.length;
            check = false;
        }
        if (this.erosion < 0){
            this.erosion = 0;
            check = false;
        }
        if (this.erosion > this.getNextLevelErosion()){
            this.erosion = this.isMaxLevel() ? 0 : this.getNextLevelErosion() - 1;
            check = false;
        }
        if (this.erosion == this.getNextLevelErosion()) this.erosion = this.getNextLevelErosion() - 1;
        return check;
    }

    // test
    public void tick() {
        this.add(10);
    }

    public Supplier<DataCompound> getDefault() {
        return ErosionData::create;
    }

    public void writeNbt(NbtCompound nbt){
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("erosion", this.erosion);
        nbtCompound.putInt("erosionLevel", this.erosionLevel);
        nbt.put(DATA_TAG, nbtCompound);
    }

    public void readNbt(NbtCompound nbt){
        NbtCompound nbtCompound = nbt.getCompound(DATA_TAG);
        this.erosion = nbtCompound.getInt("erosion");
        this.erosionLevel = nbtCompound.getInt("erosionLevel");
        // 数据检测
        if (this.checkAndFixData()) LOGGER.warn(Text.translatable(DATA_EXCEPTION));
    }

    @Override
    public String getDataTag() {
        return DATA_TAG;
    }

    /** 侵蚀度改变方式 */
    public enum ErosionChangeType implements StringIdentifiable {
        /** 保持进度 */
        PROGRESS("progress"),
        /** 归零 */
        CLEAN("clean"),
        /** 保持原值 */
        KEEP("keep");
        private final String id;
        ErosionChangeType(String id){ this.id = id;}
        public static final StringIdentifiable.Codec<ErosionChangeType> CODEC =
                StringIdentifiable.createCodec(ErosionChangeType::values);

        @Override
        public String asString() {
            return this.id;
        }
    }
}
