package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
import campionebase.erosionera.fluid.ManaFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

@Translation
public class ErErFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ErosionEra.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ErosionEra.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ErosionEra.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);
    public static void register(IEventBus bus){
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
    //region ========= 魔力液 =========
    @Translation.Key(prefix = "fluid_type", value = "魔力")
    public static final RegistryObject<FluidType> MANA_TYPE = FLUID_TYPES.register("mana",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid_type.erosionera.mana")
                    .density(1100)
                    .viscosity(1200)
                    .temperature(300)
                    .canConvertToSource(false)
                    .lightLevel(6)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
    ){
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public int getTintColor() {
                    return 0x8F55AAFF;
                }
                @Override
                public ResourceLocation getStillTexture() {
                    return ResourceLocation.parse("block/water_still");
                }
                @Override
                public ResourceLocation getFlowingTexture() {
                    return ResourceLocation.parse("block/water_flow");
                }
                @Override
                public ResourceLocation getOverlayTexture() {
                    return ResourceLocation.parse("block/water_overlay");
                }
                @Override
                public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                    return ResourceLocation.parse("textures/misc/underwater.png");
                }
            });
        }
    });

    public static final RegistryObject<FlowingFluid> MANA_SOURCE = FLUIDS.register("mana_source", ManaFluid.Source::new);
    public static final RegistryObject<FlowingFluid> MANA_FLOWING = FLUIDS.register("mana_flowing", ManaFluid.Flowing::new);
    @Translation.Key(prefix = "block", value = "魔力池")
    public static final RegistryObject<LiquidBlock> MANA_LIQUID = BLOCKS.register("mana_liquid", () -> new LiquidBlock(
            MANA_SOURCE,
            BlockBehaviour.Properties.copy(Blocks.WATER)
    ));
    @Translation.Key(prefix = "item", value = "魔力桶")
    public static final RegistryObject<BucketItem> MANA_BUCKET = ITEMS.register("mana_bucket", () -> new BucketItem(
            MANA_SOURCE,
            new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
    ));
    //endregion
}
