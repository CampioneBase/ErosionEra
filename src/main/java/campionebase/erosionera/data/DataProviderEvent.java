package campionebase.erosionera.data;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.blockstate.HorizontalBlockStateProvider;
import campionebase.erosionera.data.lang.MultiLanguageProvider;
import campionebase.erosionera.data.lang.Translation;
import campionebase.erosionera.data.model.block.BedBlockModelProvider;
import campionebase.erosionera.data.model.item.NormalItemModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(
        modid = ErosionEra.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class DataProviderEvent {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        // 生成方块模型
        generator.addProvider(event.includeClient(), new BedBlockModelProvider(output, helper));
        // 生成物品模型
        generator.addProvider(event.includeClient(), new NormalItemModelProvider(output, helper));
        // 生成方块状态
        generator.addProvider(event.includeServer(), new HorizontalBlockStateProvider(output, helper));
        // 生成翻译文件
        generator.addProvider(event.includeClient(), new MultiLanguageProvider(output, Translation.Language.ZH_CN.locale));
    }
}
