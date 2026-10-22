package campionebase.erosionera.item;

import campionebase.erosionera.ErosionEra;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMagicWeaponItem extends ManaItem {
    public AbstractMagicWeaponItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        // 开始蓄力
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.sidedSuccess(itemStack, !level.isClientSide);
    }

    @Override
    public void onUseTick(
            @NotNull Level level,
            @NotNull LivingEntity entity,
            @NotNull ItemStack itemStack,
            int remainTicks// 剩余持续时长， ojng, WTF?
    ) {
        if(!level.isClientSide){
            int mana = this.getUseDuration(itemStack) - remainTicks;
            ErosionEra.LOGGER.debug("Charging Mana: {}", mana);
        }
    }
    @Override
    public void inventoryTick(
            @NotNull ItemStack itemStack,
            @NotNull Level level,
            @NotNull Entity owner,
            int slot,
            boolean isFocused
    ) {
        super.inventoryTick(itemStack, level, owner, slot, isFocused);
    }

    @Override
    public void releaseUsing(
            @NotNull ItemStack itemStack,
            @NotNull Level level,
            @NotNull LivingEntity entity,
            int remainTicks// 剩余持续时长， ojng, WTF?
    ) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        int mana = this.getUseDuration(itemStack) - remainTicks;
        this.releaseMagic(serverLevel, itemStack, mana, entity);
    }

    protected abstract void releaseMagic(ServerLevel level, ItemStack itemStack, int mana, LivingEntity caster);

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack) {
        return 65535;
    }
}
