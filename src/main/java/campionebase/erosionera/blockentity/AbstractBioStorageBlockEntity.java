package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.IBioStorage;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBioStorageBlockEntity extends AbstractBioMachineBlockEntity implements IBioStorage {
    public static final String TAG_INVENTORY = "inventory";
    // 更新广播间隔
    public static final int BROADCAST_INTERVAL = 10;
    private int tickCounter = 0;
    private boolean storageDirty = false;

    public AbstractBioStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void markStorageDirty() {
        this.storageDirty = true;
    }

    public void forceBroadcast() {
        this.storageDirty = true;
        this.tickCounter = BROADCAST_INTERVAL - 1;
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState blockState, T blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(blockEntity instanceof AbstractBioStorageBlockEntity bioStorage)) return;

        if (!bioStorage.storageDirty) return;
        if (bioStorage.tickCounter++ < BROADCAST_INTERVAL) return;
        bioStorage.tickCounter = 0;
        bioStorage.storageDirty = false;

        BioMachineryService.broadcastBioMachineUpdate(serverLevel, BioMachineData.of(bioStorage));
    }
}
