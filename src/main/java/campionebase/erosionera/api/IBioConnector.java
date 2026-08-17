package campionebase.erosionera.api;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = ErosionEra.MODID)
public interface IBioConnector {
    @NotNull BlockPos getBlockPos();

    @Nullable IBioMachine getMachine();

    @SubscribeEvent
    static void onBlockBreak(BlockEvent.BreakEvent event){
        if (event.getLevel() instanceof ServerLevel level){
            BlockPos pos = event.getPos();
            if (level.getBlockEntity(pos) instanceof IBioConnector connector){
                BioMachineryService.removeNode(level, connector);
            }
        }
    }
}
