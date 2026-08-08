package campionebase.erosionera.item;

import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.client.event.Observable;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BioWireItem extends Item implements Observable.Item {
    public static final String TAG_SELECTION_POS = "selection_pos";
    public static final String TAG_SELECTION_LEVEL = "selection_level";
    public static final int MAX_LINKING_DISTANCE = 32;

    public BioWireItem() {
        super(new Properties());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        return tag != null && (tag.contains(TAG_SELECTION_POS));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains(TAG_SELECTION_POS) && player.isShiftKeyDown()) {
            tag.remove(TAG_SELECTION_POS);
            tag.remove(TAG_SELECTION_LEVEL);
            player.displayClientMessage(Component.literal("Cancel Linking"), true);
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.CONSUME;
        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (!(blockEntity instanceof AbstractBioConnectorBlockEntity secondLinked)) {
            player.displayClientMessage(Component.literal("Invalid block"), true);
            return InteractionResult.CONSUME;
        }
        ItemStack itemStack = context.getItemInHand();
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains(TAG_SELECTION_LEVEL)){
            if (!tag.getString(TAG_SELECTION_LEVEL).equals(player.level().dimension().registry().toString())) {
                player.displayClientMessage(Component.literal("Choose at different dimension"), true);
                return InteractionResult.CONSUME;
            }
        }
        if (tag.contains(TAG_SELECTION_POS)){
            // 已经选取连接点时
            BlockPos pos0 = this.getLinkingPos(tag);
            // 如果为同一坐标点
            if (pos.equals(pos0)) {
                player.displayClientMessage(Component.literal("Choose a same position"), true);
                return InteractionResult.CONSUME;
            }
            int distance = this.getDistance(pos, pos0);
            if (distance > MAX_LINKING_DISTANCE){
                // 距离太远
                player.displayClientMessage(Component.literal("Too far for linking"), true);
                tag.remove(TAG_SELECTION_POS);
                tag.remove(TAG_SELECTION_LEVEL);
                return InteractionResult.CONSUME;
            }
            // -- 连接两点 --
            if (context.getLevel() instanceof ServerLevel server){
                BlockEntity parent = server.getBlockEntity(pos0);
                if (parent instanceof AbstractBioConnectorBlockEntity bioParent) {
                    if (BioMachineryService.tryConnectNodes(server, bioParent, secondLinked)){
                        player.displayClientMessage(Component.literal(String.format( "Connect two positions. From:%d,%d,%d To:%d,%d,%d",
                                pos0.getX(), pos0.getY(), pos0.getZ(), pos.getX(), pos.getY(), pos.getZ())), true);
                    } else {
                        player.displayClientMessage(Component.literal(String.format( "Disconnect two positions. From:%d,%d,%d To:%d,%d,%d",
                                pos0.getX(), pos0.getY(), pos0.getZ(), pos.getX(), pos.getY(), pos.getZ())), true);
                    }
                }
            }
            // 去除选择点
            tag.remove(TAG_SELECTION_POS);
            tag.remove(TAG_SELECTION_LEVEL);
        }
        else {
            // 没有选取连接点时
            tag.putLong(TAG_SELECTION_POS, pos.asLong());
            tag.putString(TAG_SELECTION_LEVEL, player.level().dimension().registry().toString());
        }
        return InteractionResult.SUCCESS;
    }

    private BlockPos getLinkingPos(CompoundTag tag){
        return BlockPos.of(tag.getLong(TAG_SELECTION_POS));
    }

    private int getDistance(Vec3i p1, Vec3i p2){
        return (int)Math.floor(Math.sqrt(p1.distSqr(p2)));
    }

    @Override
    public @NotNull List<Component> getInfo(@NotNull Player player, @NotNull ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains(TAG_SELECTION_POS)) return List.of();
        BlockPos pos = BlockPos.of(tag.getLong(TAG_SELECTION_POS));
        HitResult hitResult = player.pick(6.0d, 1.0f, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
            int distance = this.getDistance(hitPos, pos);
            return List.of(
                    Component.literal(String.format("Linking Pos: %d %d %d", pos.getX(), pos.getY(), pos.getZ())),
                    Component.literal("Linking Distance: ")
                            .append(Component.literal("" + distance)
                                    .withStyle(distance > MAX_LINKING_DISTANCE ? ChatFormatting.GRAY : ChatFormatting.RED)
                            )
            );
        }

        return List.of(
                Component.literal(String.format("Linking Pos: %d %d %d", pos.getX(), pos.getY(), pos.getZ()))
        );
    }
}
