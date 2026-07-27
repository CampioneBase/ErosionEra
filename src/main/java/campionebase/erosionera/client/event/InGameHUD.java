package campionebase.erosionera.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class InGameHUD {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        GuiGraphics graphics = event.getGuiGraphics();
        HitResult result = player.pick(6.0d, 1.0f, false);
        switch (result.getType()) {
            case ENTITY -> {
                Entity entity = ((EntityHitResult) result).getEntity();
                // 物品掉落物
                if (entity instanceof ItemEntity itemEntity){
                    ItemStack itemStack = itemEntity.getItem();
                    if (itemStack.getItem() instanceof Observable.Item observed){
                        showInfo(graphics, observed.getInfo(player, itemStack), w / 2 + 45, h / 2 - 10);
                    }
                }
                // 其他实体
                else if (entity instanceof Observable observed){
                    showInfo(graphics, observed.getInfo(player), w / 2 + 45, h / 2 - 10);
                }
            }
            case BLOCK -> {
                BlockPos pos = ((BlockHitResult) result).getBlockPos();
                BlockEntity blockEntity = player.level().getBlockEntity(pos);
                // 方块实体
                if (blockEntity instanceof Observable observed){
                    showInfo(graphics, observed.getInfo(player), w / 2 + 45, h / 2 - 10);
                }
            }
        }
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemInHand.isEmpty() && itemInHand.getItem() instanceof Observable.Item observed){
            showInfo(graphics, observed.getInfo(player, itemInHand), w / 2 - 145, h / 2 - 10);
        }
    }

    private static void showInfo(GuiGraphics graphics, List<Component> lines, int x, int y){
        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;

        int ly = y;
        for (Component line : lines) {
            if (line.getString().equals("empty")) continue; // Component.EMPTY
            graphics.drawString(font, line.getString(), x, ly, 0xffcccccc, false);
            ly += lineHeight;
        }
    }
}