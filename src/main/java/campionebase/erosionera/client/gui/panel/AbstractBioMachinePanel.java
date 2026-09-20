package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.IBioMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractBioMachinePanel<M extends IBioMachine> implements Renderable, LayoutElement, GuiEventListener {
    protected final BioMachineType<M> type;
    protected final Screen screen;
    @NotNull
    protected final List<BioMachineData> entries = new LinkedList<>();
    protected int selectedIndex = -1;

    public int x, y, width, height;
    private boolean focused = false;
    private boolean closed = false;

    public AbstractBioMachinePanel(BioMachineType<M> type, Screen screen){
        this.type = type;
        this.screen = screen;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) { }

    public void refreshData(List<BioMachineData> dataList){
        BioMachineData current = null;
        if (this.selectedIndex != -1){
            current = this.getSelectedData();
        }
        this.entries.clear();
        this.entries.addAll(dataList);
        this.sortData();
        if (current != null){
            // 直接尝试还原选项
            int index = this.indexOf(current.pos());
            if (index != -1) {
                this.selectedIndex = index;
                return;
            }
        }
        this.select(-1);
    }

    public void sortData(){
        this.entries.sort(Comparator.comparing(BioMachineData::pos));
    }

    @Nullable
    public BioMachineData findData(BlockPos pos){
        for (BioMachineData data: this.entries) {
            if (data.pos().equals(pos)) return data;
        }
        return null;
    }

    public int indexOf(BlockPos pos){
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).pos().equals(pos)){
                return i;
            }
        }
        return -1;
    }

    public boolean isEmpty(){
        return this.entries.isEmpty();
    }

    public boolean isVisible() {
        // 当面板内没有元素时，将面板隐藏起来而不直接移除，避免面板反复创建
        return !this.isEmpty() && !this.closed;
    }

    public void handleUpdate(BioMachineData update){
        assert update.type().equals(this.type) : "update data has wrong type." +
                " Required: " + this.type.getDescriptionId() +
                ", Provided: " + update.type().getDescriptionId();
        BioMachineData oldData = this.findData(update.pos());
        if (oldData == null) return;
        int index = this.entries.indexOf(oldData);
        if (index < 0) return;
        this.entries.set(index, update);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawString(Minecraft.getInstance().font,
                this.type.getDescriptionId() + " rendering"
                , this.x, this.y,
                0xffc0c0c0);
    }

    public boolean isFocused(){
        return this.focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void selectNext(){
        if (this.entries.isEmpty()) {
            this.selectedIndex = -1;
            return;
        }
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
        if (index >= this.entries.size() || index < 0) index = -1;
        if (index != this.selectedIndex){
            this.selectedIndex = index;
            this.onSelectChanged();
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    protected boolean isSelectionValid(int index){
        return index >= -1 && index < this.entries.size();

    }

    protected void onSelectChanged() {}

    @Nullable
    public BioMachineData getSelectedData(){
        return this.getSelectedData(this.selectedIndex);
    }

    @Nullable
    public BioMachineData getSelectedData(int index){
        if (index >= 0 && index < this.entries.size()){
            return this.entries.get(index);
        }
        return null;
    }

    @Nullable
    public M getSelectedInstance(Level level){
        BioMachineData data = this.getSelectedData();
        if (data == null) return null;
        return this.getInstanceAt(level, data.pos());
    }

    @Nullable
    protected abstract M getInstanceAt(Level level, BlockPos pos);

    public void close(){
        this.closed = true;
    }

    public void open(){
        this.closed = false;
    }

    @Override
    public int getTabOrderGroup() {
        return this.type.getOrderWeight();
    }
}
