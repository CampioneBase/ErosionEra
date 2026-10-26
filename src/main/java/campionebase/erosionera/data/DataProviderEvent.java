package campionebase.erosionera.data;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.blockstate.NormalBlockStateProvider;
import campionebase.erosionera.data.lang.MultiLanguageProvider;
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
        // 生成方块状态
        generator.addProvider(event.includeServer(), new NormalBlockStateProvider(output, helper));
        // 生成物品模型
        generator.addProvider(event.includeClient(), new NormalItemModelProvider(output, helper));
        // 生成翻译文件：只处理本模组自身的翻译来源
        for (String locale : MultiLanguageProvider.collectLanguages(ErosionEra.MODID)) {
            generator.addProvider(event.includeClient(),
                    new MultiLanguageProvider(output, ErosionEra.MODID, locale));
        }
    }
}