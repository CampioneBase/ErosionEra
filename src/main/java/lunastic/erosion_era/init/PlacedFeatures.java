package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CarvingMaskPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;
import java.util.function.Predicate;

public abstract class PlacedFeatures {
    // 通常微光柱
    public static final PlacedFeature SHIMMER_PILLAR_NORMAL = register("shimmer_pillar_normal",
            ConfiguredFeatures.SHIMMER_PILLAR_NORMAL,
            GenerationStep.Feature.SURFACE_STRUCTURES,
            BiomeSelectors.all(),
            // 区块内随机水平位置
            SquarePlacementModifier.of(),
            // 2% 生成概率
            RarityFilterPlacementModifier.of(50),

            CarvingMaskPlacementModifier.of(GenerationStep.Carver.AIR)
    );

    // 大型微光柱
    public static final PlacedFeature SHIMMER_PILLAR_LARGE = register("shimmer_pillar_large",
            ConfiguredFeatures.SHIMMER_PILLAR_LARGE,
            GenerationStep.Feature.SURFACE_STRUCTURES,
            BiomeSelectors.includeByKey(BiomeKeys.EROSION_AREA),
            SquarePlacementModifier.of(),
            RarityFilterPlacementModifier.of(100)
    );
    /*
    摘自 https://www.bilibili.com/read/cv22935348 (原文版本是1.19.4， 此处用1.19.2所对应的类名)
    == 地物生成阶段 ==
    这 11 个阶段中，除了 STRONGHOLDS 阶段没有任何作用外，其他阶段都有各自的作用:
    RAW_GENERATION:         用于生成其他地物依赖的位置。原版中这里只有一个末地小岛的地物。
    LAKES:                  生成湖。在 1.18 之后水湖已经被含水层替代，目前这里只剩下熔岩湖地物。
    LOCAL_MODIFICATIONS:    生成一些对地形影响较大的地物。原版中紫晶洞、冰山等会在这个阶段生成。
    UNDERGROUND_STRUCTURES: 生成地下的结构。原版中废弃矿井、化石、埋藏的宝藏在这个阶段生成。
    SURFACE_STRUCTURES:     生成地表结构。大部分结构都在这个阶段生成，沙漠水井和冰刺也是在这个阶段生成。
    STRONGHOLDS:            无作用。要塞在地表结构生成时生成。
    UNDERGROUND_ORES:       生成地下矿物。所有的团簇都在这个阶段生成。
    UNDERGROUND_DECORATION: 生成地下装饰。远古城市在这个阶段生成，幽匿斑块也是这个阶段生成的。
    FLUID_SPRINGS:          生成涌泉。
    VEGETAL_DECORATION:     生成植物装饰。树木、花草斑块等都是在这个阶段生成。
    TOP_LAYER_MODIFICATION: 顶层修改。这个阶段只有冰冻顶层地物，用于生成冰雪表面。

    == 地物生成修饰器 ==
    CountPlacement:                  指定数字区间的尝试次数。
    NoiseBasedCountPlacement:        使用生物群系噪声 BIOME_INFO_NOISE 的噪声值进行放置。每当噪声值上升一个区间就加 1。
    NoiseThresholdCountPlacement:    使用生物群系噪声 BIOME_INFO_NOISE 的噪声值进行放置。如果噪声值超过某一个值就使用设置的第一个值，否则使用第二个。
    CountMultilayerPlacementModifier:尝试生成指定个数的点，随机分配到区块内的水平坐标。对于每个水平坐标，找到最高的地表位置作为最终的放置位置。
    EnvironmentScanPlacement:        对于输入的放置点，尝试在指定步数内按指定方向找到一个满足指定条件的位置作为最终放置点。
    HeightmapPlacement:              将放置点移动到水平坐标上的某一高度图高度处。
    HeightRangePlacement:            将放置点移动到指定的高度上。
    SquarePlacement:                 将放置点水平移动到区块的某个位置上。
    RandomOffsetPlacement:           以放置点为中心，将放置点移动到指定高度和宽度空间内的某个位置。
    CarvingMaskPlacement:            获取整个区块内所有包含指定雕刻标记的位置。
    BiomePlacement:                  检查放置点的生物群系，如果群系不对应则放弃在此点的放置。验证时使用从生物群系单元缩放到方块后的生物群系。
    BlockFilterPlacement:            如果放置点的方块不通过方块谓词测试，则此放置点无效。
    RarityFilterPlacement:           如果随机数大于 1/chance ，则此放置点无效。相当于平均每 chance 次生成才能成功一次。
    SurfaceThresholdFilterPlacement: 当生成点位于某个高度图高度偏移的某个区间时生成点才有效。
    SurfaceWaterDepthFilterPlacement:当放置点水平坐标的最高非空气方块和最高可阻挡方块的高度差值小于某个值时，或简单来说此位置最高位置上的水深小于某个值时，此放置点有效。

    生物群系噪声 BIOME_INFO_NOISE 是一个和种子无关的噪声，也就是说在不同的世界中，如果有一个位置的生物群系相同，那么使用后两个放置修饰器的同一种地物的生成尝试次数就是相同的。
    生成地物时默认位置是在区块的最低子区块的 (0, 0, 0) 处。为了让区块内的地物合理放置，还需要其他放置修饰器用于修改地物的放置点。
    */
    public static PlacedFeature register(
            String id,
            ConfiguredFeature<?, ?> configuredFeature,
            GenerationStep.Feature step,
            Predicate<BiomeSelectionContext> biomeSelector,
            PlacementModifier... placementModifiers
    ){
        BiomeModifications.addFeature(biomeSelector, step, RegistryKey.of(Registry.PLACED_FEATURE_KEY, ErosionEraMod.identifier(id)));
        return Registry.register(
                BuiltinRegistries.PLACED_FEATURE,
                ErosionEraMod.identifier(id),
                new PlacedFeature(RegistryEntry.of(configuredFeature), List.of(placementModifiers))
        );
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Features Loading......");
    }
}
