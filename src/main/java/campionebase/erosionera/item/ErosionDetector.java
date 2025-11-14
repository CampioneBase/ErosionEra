package campionebase.erosionera.item;

import campionebase.erosionera.init.ErErAttributes;
import campionebase.erosionera.init.ErErItems;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ErosionDetector extends Item {
    public ErosionDetector() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
//        InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
//        sendErosionInfo(world,
//                new Vec3((entity.getX()), (entity.getY()), (entity.getZ())),
//                entity.getDisplayName().getString(),
//                entity.getAttribute(ErErAttributes.EROSION_LEVEL.get()).getBaseValue(),
//                entity.getAttribute(ErErAttributes.EROSION_VALUE.get()).getBaseValue()
//        );
//        return ar;
//    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND)
            return;
        ItemStack itemStack = event.getEntity().getItemInHand(event.getHand());
        if (itemStack.is(ErErItems.EROSION_DETECTOR.get())){
            if(event.getTarget() != null && event.getTarget() instanceof LivingEntity entity){
                Player player = event.getEntity();
                sendErosionInfo(event.getLevel(),
                        new Vec3((player.getX()), (player.getY()), (player.getZ())),
                        entity.getDisplayName().getString(),
                        entity.getAttribute(ErErAttributes.EROSION_LEVEL.get()).getBaseValue(),
                        entity.getAttribute(ErErAttributes.EROSION_VALUE.get()).getBaseValue()
                );
            }
        }
    }

    private static void sendErosionInfo(Level world, Vec3 vec3, String name, double level, double value){
        if (world instanceof ServerLevel serverlevel)
            serverlevel.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, vec3, Vec2.ZERO, serverlevel, 4, "", Component.literal(""), serverlevel.getServer(), null).withSuppressedOutput(),
                    "title @p actionbar \""
                            + name + " "
                            + Component.translatable("attribute.erosionera.erosion_value").getString()
                            + String.format(": lv.%.0f %.0f EE\"", level, value));
    }
}
