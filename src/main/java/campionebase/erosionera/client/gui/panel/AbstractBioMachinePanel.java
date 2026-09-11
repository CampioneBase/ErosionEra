package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.IBioMachine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractBioMachinePanel<M extends IBioMachine> {
    @FunctionalInterface
    protected interface INBTSolution{
        @NotNull
        CompoundTag solve(CompoundTag o, CompoundTag n);
    }

    protected final BioMachineType<M> type;
    protected final Level level;
    @NotNull
    protected final List<BioMachineData> entries = new LinkedList<>();
    protected int selectedIndex = -1;

    public int x, y, width, height;

    public AbstractBioMachinePanel(BioMachineType<M> type, Level level){
        this.type = type;
        this.level = level;
    }

    public void refreshData(List<BioMachineData> dataList){
        this.clear();
        this.entries.addAll(dataList);
        this.sortData();
    }

    public void sortData(){
        this.entries.sort(Comparator.comparing(BioMachineData::pos));
    }

    public void updateData(BioMachineData data, INBTSolution solution){
        assert solution != null;
        BioMachineData oldData = this.findData(data.pos());
        if (oldData == null) return;
        int index = this.entries.indexOf(oldData);
        if (index < 0) return;
        BioMachineData newData = new BioMachineData(
                this.type,
                data.pos(),
                solution.solve(oldData.customData(), data.customData())
        );
        this.entries.remove(index);
        this.entries.add(index, newData);
    }

    public void addData(BioMachineData data){
        this.entries.add(data);
        this.sortData();
    }

    @Nullable
    public BioMachineData findData(BlockPos pos){
        for (BioMachineData data: this.entries) {
            if (data.pos().equals(pos)) return data;
        }
        return null;
    }

    public boolean isEmpty(){
        return this.entries.isEmpty();
    }

    public boolean isVisible() {
        // 当面板内没有元素时，将面板隐藏起来而不直接移除，避免面板反复创建
        return !this.isEmpty();
    }

    public void handleUpdate(BioMachineData update) {}

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY);

    protected boolean isFocus(int mouseX, int mouseY){
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public void selectNext(){
        if (this.entries.isEmpty()) { this.selectedIndex = -1; return; }
        int index = this.selectedIndex;
        int limit = 0;
        while (limit++ < this.entries.size() + 1){
            index ++;
            if (index > this.entries.size() - 1) index = -1;
            if (index == -1 || this.isSelectionValid(index)) {
                this.select(index);
                return;
            }
        }
    }

    public void selectPrev(){
        if (this.entries.isEmpty()) { this.selectedIndex = -1; return; }
        int index = this.selectedIndex;
        int limit = 0;
        while (limit++ < this.entries.size() + 1){
            index --;
            if (index < -1) index = this.entries.size() -1;
            if (index == -1 || this.isSelectionValid(index)){
                this.select(index);
                return;
            }
        }
    }

    protected void select(int index){
        if (index != this.selectedIndex){
            this.selectedIndex = index;
            this.onSelectChanged();
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    protected boolean isSelectionValid(int index){
        return true;
    }

    protected void onSelectChanged() {}

    @Nullable
    public BioMachineData getSelectedEntry(){
        if (this.selectedIndex > 0 && this.selectedIndex < this.entries.size() - 1){
            return this.entries.get(this.selectedIndex);
        }
        return null;
    }

    @Nullable
    public M getSelectedInstance(Level level){
        BioMachineData data = this.getSelectedEntry();
        if (data == null) return null;
        return this.getInstanceAt(level, data.pos());
    }

    @Nullable
    protected abstract M getInstanceAt(Level level, BlockPos pos);

    public void clear(){
        this.entries.clear();
    };
}
