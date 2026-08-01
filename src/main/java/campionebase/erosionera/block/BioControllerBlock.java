package campionebase.erosionera.block;

import campionebase.erosionera.blockentity.BioControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class BioControllerBlock extends BioMachineryBlock implements EntityBlock {
    public BioControllerBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(1.0f)
                .pushReaction(PushReaction.IGNORE)
        );
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof BioControllerBlockEntity controller))
            return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel){
            if (player instanceof ServerPlayer serverPlayer){
                // serverPlayer.openMenu(controller);
                NetworkHooks.openScreen(serverPlayer, controller, pos);

            }
        } else {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioControllerBlockEntity(pos, blockState);
    }
}
