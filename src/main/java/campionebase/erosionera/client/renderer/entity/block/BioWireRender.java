package campionebase.erosionera.client.renderer.entity.block;

import campionebase.erosionera.blockentity.BioConnectorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Set;

public class BioWireRender<T extends BioConnectorBlockEntity> implements BlockEntityRenderer<T> {
    public BioWireRender(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull T blockEntity,
                       float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        Set<BlockPos> neighbors = blockEntity.getNeighbors();
        if (neighbors.isEmpty()) return;

        BlockPos currentPos = blockEntity.getBlockPos();
        // todo 暂时使用绘制直线方式表示导线
        int color = 0xff8B4513;

        RenderSystem.lineWidth(3.0f);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        float startX = 0.5f;
        float startY = 0.5f;
        float startZ = 0.5f;

        for (BlockPos neighbor : neighbors) {
            float endX = neighbor.getX() - currentPos.getX() + 0.5f;
            float endY = neighbor.getY() - currentPos.getY() + 0.5f;
            float endZ = neighbor.getZ() - currentPos.getZ() + 0.5f;

            consumer.vertex(matrix, startX, startY, startZ)
                    .color(color)
                    .normal(0, 1, 0)
                    .endVertex();

            consumer.vertex(matrix, endX, endY, endZ)
                    .color(color)
                    .normal(0, 1, 0)
                    .endVertex();
        }
        RenderSystem.lineWidth(1.0f);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BioConnectorBlockEntity blockEntity) {
        return true;
    }
}
