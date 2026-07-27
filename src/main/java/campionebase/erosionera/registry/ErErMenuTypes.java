package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.inventory.BioControllerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ErErMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ErosionEra.MODID);

    public static final RegistryObject<MenuType<BioControllerMenu>> BIO_CONTROLLER_MENU =
            REGISTRY.register("bio_controller", () -> IForgeMenuType.create(BioControllerMenu::new));
}
