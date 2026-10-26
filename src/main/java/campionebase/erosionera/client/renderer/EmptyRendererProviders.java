package campionebase.erosionera.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class EmptyRendererProviders {
    private EmptyRendererProviders() {
        throw new UnsupportedOperationException();
    }

    public static final EntityRendererProvider<Entity> ENTITY = ctx -> new EntityRenderer<>(ctx) {
            @Override
            public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
                return TextureAtlas.LOCATION_PARTICLES;
            }

            @Override
            protected boolean shouldShowName(@NotNull Entity entity) {
                return false;
            }

            @Override
            public boolean shouldRender(@NotNull Entity entity, @NotNull Frustum frustum, double px, double py, double pz) {
                return false;
            }
    };

    public static final BlockEntityRendererProvider<BlockEntity> BLOCK_ENTITY = ctx -> new BlockEntityRenderer<BlockEntity>() {
        @Override
        public void render(
                @NotNull BlockEntity blockEntity,
                float partialTick,
                @NotNull PoseStack poseStack,
                @NotNull MultiBufferSource bufferSource,
                int packedLight,
                int packedOverlay
        ) {}

        @Override
        public boolean shouldRender(@NotNull BlockEntity entity, @NotNull Vec3 position) {
            return false;
        }

        @Override
        public boolean shouldRenderOffScreen(@NotNull BlockEntity blockEntity) {
            return false;
        }
    };
}
