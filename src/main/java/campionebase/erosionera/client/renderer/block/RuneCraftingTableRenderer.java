package campionebase.erosionera.client.renderer.block;

import campionebase.erosionera.block.entity.RuneCraftingTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 符文合成台的物品渲染。
 *
 * <ul>
 *   <li><b>核心物品</b>：悬浮在 (0.5, 0.8, 0.5)，始终正面朝向本地玩家。</li>
 *   <li><b>材料</b>：围绕 (0.5, 0.6, 0.5)、半径 0.4 公转，始终正面朝向核心物品所在点。</li>
 *   <li><b>高亮</b>：玩家瞄准顶面的核心区域时，用白色方框标出该区域。</li>
 * </ul>
 *
 * <p>朝向用 `ItemDisplayContext.FIXED`（原版展示框的上下文，物品正面为 +Z），
 * 再按 yaw / pitch 把 +Z 旋到目标方向。
 */
@OnlyIn(Dist.CLIENT)
public class RuneCraftingTableRenderer implements BlockEntityRenderer<RuneCraftingTableBlockEntity> {

    // 核心物品
    private static final float CORE_X = 0.5F;
    private static final float CORE_Y = 1.2F;
    private static final float CORE_Z = 0.5F;
    private static final float CORE_SCALE = 0.8F;

    // 材料公转
    private static final float ORBIT_CENTER_X = 0.5F;
    private static final float ORBIT_CENTER_Y = 0.8F;
    private static final float ORBIT_CENTER_Z = 0.5F;
    private static final float ORBIT_RADIUS = 0.8F;
    private static final float MATERIAL_SCALE = 0.6F;
    /** 公转速度：度 / tick（约 4 秒一圈）。 */
    private static final float ORBIT_DEGREES_PER_TICK = 1.5F;
    // 浮动幅度
    private static final float FLOAT_AMPLITUDE = 0.08F;
    // 浮动速度
    public static final float FLOAT_SPEED = 0.1F;

    private final ItemRenderer itemRenderer;

    public RuneCraftingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            @NotNull RuneCraftingTableBlockEntity blockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        this.renderCore(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
        this.renderMaterials(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        this.renderCoreHighlight(blockEntity, poseStack, bufferSource);
    }

    // ------------------------------------------------------------------ 核心

    private void renderCore(RuneCraftingTableBlockEntity blockEntity, PoseStack poseStack,
                            MultiBufferSource bufferSource, int light, int overlay) {
        ItemStack core = blockEntity.coreStack();
        if (core.isEmpty()) {
            return;
        }
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos pos = blockEntity.getBlockPos();
        double dx = camera.x - (pos.getX() + CORE_X);
        double dy = camera.y - (pos.getY() + CORE_Y);
        double dz = camera.z - (pos.getZ() + CORE_Z);

        poseStack.pushPose();
        poseStack.translate(CORE_X, CORE_Y, CORE_Z);
        this.faceDirection(poseStack, dx, 0, dz);
        poseStack.scale(CORE_SCALE, CORE_SCALE, CORE_SCALE);
        this.renderItem(core, poseStack, bufferSource, light, overlay, 0);
        poseStack.popPose();
    }

    // ------------------------------------------------------------------ 材料

    private void renderMaterials(RuneCraftingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int light, int overlay) {
        List<ItemStack> materials = new ArrayList<>();
        for (int slot = RuneCraftingTableBlockEntity.CORE_SLOT + 1;
             slot < RuneCraftingTableBlockEntity.SLOT_COUNT; slot++) {
            ItemStack stack = blockEntity.getItem(slot);
            if (!stack.isEmpty()) {
                materials.add(stack);
            }
        }
        if (materials.isEmpty()) {
            return;
        }

        long gameTime = blockEntity.getLevel() == null ? 0L : blockEntity.getLevel().getGameTime();
        float baseAngle = (gameTime + partialTick) * ORBIT_DEGREES_PER_TICK;
        float step = 360.0F / materials.size();

        for (int i = 0; i < materials.size(); i++) {
            double fy = Math.sin(gameTime * FLOAT_SPEED + i) * FLOAT_AMPLITUDE;

            double angle = Math.toRadians(baseAngle + step * i);
            double x = ORBIT_CENTER_X + Math.cos(angle) * ORBIT_RADIUS;
            double z = ORBIT_CENTER_Z + Math.sin(angle) * ORBIT_RADIUS;

            // 朝向核心物品所在点
            double dx = CORE_X - x;
            double dy = CORE_Y - ORBIT_CENTER_Y;
            double dz = CORE_Z - z;

            poseStack.pushPose();
            poseStack.translate(x, ORBIT_CENTER_Y + fy, z);
            this.faceDirection(poseStack, dx, dy, dz);
            poseStack.scale(MATERIAL_SCALE, MATERIAL_SCALE, MATERIAL_SCALE);
            this.renderItem(materials.get(i), poseStack, bufferSource, light, overlay, i + 1);
            poseStack.popPose();
        }
    }

    // ------------------------------------------------------------------ 高亮

    /**
     * 当本地玩家瞄准顶面的核心区域时，画出白色方形边框。
     *
     * <p>判定与方块交互共用 {@link RuneCraftingTableBlockEntity#isCoreRegion}，
     * 保证"高亮的地方"就是"能操作核心槽的地方"。
     */
    private void renderCoreHighlight(RuneCraftingTableBlockEntity blockEntity, PoseStack poseStack,
                                     MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hit = minecraft.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        if (!blockHit.getBlockPos().equals(blockEntity.getBlockPos())) {
            return;
        }
        if (blockHit.getDirection() != Direction.UP) {
            return;
        }
        BlockPos pos = blockEntity.getBlockPos();
        double localX = blockHit.getLocation().x - pos.getX();
        double localZ = blockHit.getLocation().z - pos.getZ();
        if (!RuneCraftingTableBlockEntity.isCoreRegion(localX, localZ)) {
            return;
        }

        // 贴合顶面的极薄方块 —— 视觉上就是一圈白色方框
        AABB box = new AABB(
                RuneCraftingTableBlockEntity.CORE_REGION_MIN, 0.501D,
                RuneCraftingTableBlockEntity.CORE_REGION_MIN,
                RuneCraftingTableBlockEntity.CORE_REGION_MAX, 0.503D,
                RuneCraftingTableBlockEntity.CORE_REGION_MAX);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, consumer, box, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    // ------------------------------------------------------------------ 工具

    /**
     * 把本地 +Z 轴旋到给定方向。
     *
     * <p>`mulPose(Y)` 之后接 `mulPose(X)` 等价于 R = Ry·Rx，与推导一致：
     * 先绕 X 俯仰，再绕 Y 偏航，使 (0,0,1) 指向 (dx, dy, dz)。
     */
    private void faceDirection(PoseStack poseStack, double dx, double dy, double dz) {
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal < 1.0E-6D && Math.abs(dy) < 1.0E-6D) {
            return;
        }
        float yaw = (float) Math.toDegrees(Math.atan2(dx, dz));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }

    private void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource,
                            int light, int overlay, int seed) {
        this.itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                light,
                overlay,
                poseStack,
                bufferSource,
                Minecraft.getInstance().level,
                seed);
    }
}
