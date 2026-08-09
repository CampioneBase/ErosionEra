package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.*;
import campionebase.erosionera.data.lang.Translation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Translation(key = "block")
public class ErErBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, ErosionEra.MODID);

    @Translation.ZH_CN("活体控制器")
    public static final RegistryObject<Block> BIO_CONTROLLER =  REGISTRY.register("bio_controller", BioControllerBlock::new);
    @Translation.ZH_CN("活体操控台")
    public static final RegistryObject<Block> BIO_CONTROLLER_BED = REGISTRY.register("bio_controller_bed", BioControllerBedBlock::new);
    @Translation.ZH_CN("活体连接器")
    public static final RegistryObject<Block> BIO_CONNECTOR = REGISTRY.register("bio_connector", BioConnectorBlock::new);
    @Translation.ZH_CN("活体节点")
    public static final RegistryObject<Block> BIO_NODE = REGISTRY.register("bio_node", BioNodeBlock::new);
    @Translation.ZH_CN("活体营养容器")
    public static final RegistryObject<Block> BIO_NUTRITION_TANK = REGISTRY.register("bio_nutrition_tank", BioNutritionTankBlock::new);
    @Translation.ZH_CN("活体摄像头")
    public static final RegistryObject<Block> BIO_CAMERA = REGISTRY.register("bio_camera", BioCameraBlock::new);
    @Translation.ZH_CN("活体红石")
    public static final RegistryObject<Block> BIO_REDSTONE = REGISTRY.register("bio_redstone", BioRedstoneBlock::new);
}
