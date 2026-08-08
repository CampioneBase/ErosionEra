package campionebase.erosionera.block;

import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.blockentity.BioControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioControllerBlock extends BioMachineryBlock implements EntityBlock {
    /**
     * @deprecated 此方块状态仅用于方块形状判断，不应参与占用逻辑判断
     * <p>
     * 具体占用事实请使用方块实体的{@link IBioController#getUser()} 进行判断
     */
    @Deprecated
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public BioControllerBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(1.0f)
                .pushReaction(PushReaction.IGNORE)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(OCCUPIED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
         builder.add(OCCUPIED);
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

        if (controller.getUser() != null) {
            player.displayClientMessage(Component.literal("Bio Controller Occupied"), true);
        } else {
            level.setBlock(pos, blockState.setValue(OCCUPIED, true), UPDATE_ALL);
            if (player instanceof ServerPlayer serverPlayer){
                // serverPlayer.openMenu(controller);
                NetworkHooks.openScreen(serverPlayer, controller, pos);
                Vec3 position = pos.above().getCenter();
                serverPlayer.teleportTo(position.x, position.y, position.z);
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioControllerBlockEntity(pos, blockState);
    }
}
