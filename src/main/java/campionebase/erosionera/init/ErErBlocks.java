package campionebase.erosionera.init;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.BioControllerBedBlock;
import campionebase.erosionera.data.lang.Translation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Translation(key = "block")
public class ErErBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, ErosionEra.MODID);
    @Translation.ZH_CN("生物控制台")
    public static final RegistryObject<Block> BIO_CONTROLLER_BED = REGISTRY.register("bio_controller_bed", BioControllerBedBlock::new);
}
