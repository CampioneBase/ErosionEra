package campionebase.erosionera.data.blockstate;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.BioConnectorBlock;
import campionebase.erosionera.registry.ErErBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class BioConnectorBlockStateProvider extends BlockStateProvider {
    public BioConnectorBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ErosionEra.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        Block block = ErErBlocks.BIO_CONNECTOR.get();
        String name = ErErBlocks.BIO_CONNECTOR.getId().getPath();

        // ---------- 构建模型 ----------
        ModelBuilder<?> modelBuilder = models().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/" + name))
                .texture("texture", modLoc("block/" + name));

        // ---- 底座元素 ----
        ModelBuilder<?>.ElementBuilder baseElem = modelBuilder.element();
        baseElem.from(5, 0, 5).to(11, 1, 11);
        for (Direction dir : Direction.values()) {
            float[] uv;
            if (dir == Direction.UP || dir == Direction.DOWN) {
                uv = new float[]{0, 0, 6, 6};      // 顶/底
            } else {
                uv = new float[]{0, 0, 6, 1};      // 侧边
            }
            baseElem.face(dir)
                    .uvs(uv[0], uv[1], uv[2], uv[3])
                    .texture("#texture")
                    .cullface(dir);
        }

        // ---- 柱子元素 ----
        ModelBuilder<?>.ElementBuilder pillarElem = modelBuilder.element();
        pillarElem.from(6, 1, 6).to(10, 8, 10);
        for (Direction dir : Direction.values()) {
            float[] uv;
            if (dir == Direction.UP || dir == Direction.DOWN) {
                uv = new float[]{12, 0, 16, 4};     // 顶部（底部不可见可忽略）
            } else {
                uv = new float[]{0, 8, 4, 16};      // 侧边
            }
            pillarElem.face(dir)
                    .uvs(uv[0], uv[1], uv[2], uv[3])
                    .texture("#texture")
                    .cullface(dir);
        }

        // ---------- 生成方块状态（六个朝向） ----------
        getVariantBuilder(block).forAllStates(blockState -> {
            Direction dir = blockState.getValue(BioConnectorBlock.FACING);
            int xRot = 0, yRot = 0;
            switch (dir) {
                case DOWN  -> xRot = 180;
                case SOUTH -> { xRot = -90; yRot = 0;   }
                case NORTH -> { xRot = -90; yRot = 180; }
                case WEST  -> { xRot = -90; yRot = 90;  }
                case EAST  -> { xRot = -90; yRot = -90; }
                default    -> { /* UP */ }
            }
            return ConfiguredModel.builder()
                    .modelFile(modelBuilder)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });

        // ---------- 物品模型 ----------
        itemModels().getBuilder(name).parent(modelBuilder);
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Bio Connector)";
    }
}
