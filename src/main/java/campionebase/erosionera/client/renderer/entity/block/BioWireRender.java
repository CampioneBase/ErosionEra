package campionebase.erosionera.client.renderer.entity.block;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Set;

public class BioWireRender implements BlockEntityRenderer<AbstractBioConnectorBlockEntity> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID,
            "textures/block/bio_wire.png"
    );
    private static final double WEIGHT = 10; // 悬链线参数
    public static final int LOD1_DISTANCE = 32;
    public static final int LOD2_DISTANCE = 8;

    // UV 常量
    private static final float SHELL_U_MIN = 0.0f / 16.0f;
    private static final float SHELL_V_MIN = 0.0f / 16.0f;
    private static final float SHELL_U_MAX = 12.0f / 16.0f;
    private static final float SHELL_V_MAX = 4.0f / 16.0f;

    private static final float CORE_U_MIN = 0.0f / 16.0f;
    private static final float CORE_V_MIN = 12.0f / 16.0f;
    private static final float CORE_U_MAX = 4.0f / 16.0f;
    private static final float CORE_V_MAX = 16.0f / 16.0f;
    // 导线视觉宽度
    private static final float WIDTH = 0.13f;

    public BioWireRender(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull AbstractBioConnectorBlockEntity blockEntity,
                       float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Set<BlockPos> neighbors = blockEntity.getNeighbors();
        if (neighbors.isEmpty()) return;

        BlockPos pos = blockEntity.getBlockPos();
        for (BlockPos neighborPos : neighbors) {
            // 规定导线方向，即只交由其中一个接线器渲染
            if (pos.compareTo(neighborPos) > 0) continue;
            if (!(level.getBlockEntity(neighborPos) instanceof AbstractBioConnectorBlockEntity neighbor)) continue;

            // 绝对坐标
            Vec3 start = blockEntity.getWirePos(neighborPos);
            Vec3 end = neighbor.getWirePos(blockEntity.getBlockPos());
            Vec3 offset = new Vec3(pos.getX(), pos.getY(), pos.getZ());

            // 构建坐标系
            Vec3 dir = end.subtract(start).normalize();
            Vec3 upLocal = this.getUpVector(dir);
            Vec3 rightLocal = upLocal.cross(dir).normalize();
            double totalLen = start.distanceTo(end);
            if (totalLen < 0.01) continue;

            // 判断导线是否可见
            Vec3 center = this.getCatenaryPoint(start, dir, upLocal, totalLen, 0.5);
            Frustum frustum = Minecraft.getInstance().levelRenderer.getFrustum();
            AABB cube = new AABB(start, end);
            if (cube.minY > center.y - WIDTH) cube = cube.setMinY(center.y - WIDTH); // 将导线包裹
            if (!frustum.isVisible(cube)) continue;

            // 计算悬链线点列与光源点列
            int count = Math.max(1, (int)(totalLen / 2.0) + 1);
            Vec3[] points = new Vec3[count + 1];
            int[] lights = new int[count + 1];
            for (int i = 0; i <= count; i++) {
                double t = (double) i / count;
                points[i] = getCatenaryPoint(start.subtract(offset), dir, upLocal, totalLen, t);
                lights[i] = LevelRenderer.getLightColor(level, BlockPos.containing(points[i].add(offset)));
            }

            /* 渲染调试
            this.renderDebug(start, end, poseStack, bufferSource, 0xffff0000);
            this.renderDebug(start.add(end).scale(0.5), center.subtract(pos.getX(), pos.getY(), pos.getZ()), poseStack, bufferSource, 0xffff0000);
            for (int i = 0; i < points.length - 1; i++) {
                this.renderDebug(points[i], points[i + 1], poseStack, bufferSource, 0xff00ff00);
            }
            */
            // LOD 渲染
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            double distance = camera.getPosition().distanceToSqr(center);
            if (distance > LOD1_DISTANCE * LOD1_DISTANCE) {
                this.renderLOD1(points, dir, upLocal, rightLocal, totalLen,
                        poseStack, bufferSource, lights, packedOverlay);
            } else if (distance > LOD2_DISTANCE * LOD2_DISTANCE) {
                this.renderLOD2(points, totalLen, poseStack, bufferSource, lights, packedOverlay);
            } else {
                // 暂时不需要近距离时更细致渲染
                this.renderLOD2(points, totalLen, poseStack, bufferSource, lights, packedOverlay);
            }
        }
    }

    private void renderDebug(Vec3 start, Vec3 end,
                             PoseStack poseStack,
                             MultiBufferSource bufferSource, int color)
    {
        RenderSystem.lineWidth(3.0f);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        consumer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z)
                .color(color)
                .normal(normal, 0, 0, 0)
                .endVertex();
        consumer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z)
                .color(color)
                .normal(normal, 0, 0, 0)
                .endVertex();
        RenderSystem.lineWidth(1.0f);
    }

    private Vec3 getUpVector(Vec3 dir){
        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(dir.dot(up)) > 0.99) {
            up = new Vec3(0, 0, 1);
        }
        return up.subtract(dir.scale(dir.dot(up))).normalize();
    }

    private Vec3 getCatenaryPoint(Vec3 start, Vec3 dir, Vec3 upLocal, double totalLen, double t) {
        double x = totalLen * t;
        double a = WEIGHT;
        // y = a * cosh((x - L/2) / a) - a * cosh((-L/2) / a)
        double y = a * Math.cosh((x - totalLen / 2.0) / a) - a * Math.cosh((-totalLen / 2.0) / a);
        return start.add(dir.scale(x)).add(upLocal.scale(y));
    }

    private void renderLOD1(Vec3[] points, Vec3 dir, Vec3 upLocal, Vec3 rightLocal, double totalLen,
                            PoseStack poseStack, MultiBufferSource bufferSource, int[] lights, int packedOverlay)
    {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        // 两个交叉平面：绕线段旋转 ±45度
        Vec3 axis1 = upLocal.add(rightLocal).normalize().scale(WIDTH);
        Vec3 axis2 = upLocal.subtract(rightLocal).normalize().scale(WIDTH);

        for (int i = 0; i < points.length - 1; i++) {
            Vec3 start = points[i];
            Vec3 end = points[i + 1];
            renderDoublePlane(consumer, mat, normalMat, start, end, axis1, totalLen, lights[i], lights[i + 1], packedOverlay);
            renderDoublePlane(consumer, mat, normalMat, start, end, axis2, totalLen, lights[i], lights[i + 1], packedOverlay);
        }
    }

    private void renderDoublePlane(VertexConsumer consumer, Matrix4f mat, Matrix3f normalMat,
                                   Vec3 start, Vec3 end, Vec3 axis,
                                   double totalLen, int startLight, int endLight, int overlay)
    {
        Vec3 s0 = start.subtract(axis);
        Vec3 e0 = end.subtract(axis);
        Vec3 e1 = end.add(axis);
        Vec3 s1 = start.add(axis);
        // 正面
        this.addQuad(consumer, mat, normalMat,
                s0, e0, e1, s1,
                SHELL_U_MIN, SHELL_V_MIN,
                SHELL_U_MAX, SHELL_V_MIN,
                SHELL_U_MAX, SHELL_V_MAX,
                SHELL_U_MIN, SHELL_V_MAX,
                startLight, endLight, endLight, startLight, overlay);
        // 反面
        this.addQuad(consumer, mat, normalMat,
                e0, s0, s1, e1,
                SHELL_U_MIN, SHELL_V_MIN,
                SHELL_U_MAX, SHELL_V_MIN,
                SHELL_U_MAX, SHELL_V_MAX,
                SHELL_U_MIN, SHELL_V_MAX,
                endLight, startLight, startLight, endLight, overlay);
    }

    private void renderLOD2(Vec3[] points, double totalLen,
                            PoseStack poseStack, MultiBufferSource bufferSource,
                            int[] lights, int overlay) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        int length = points.length - 1;

        for (int seg = 0; seg < length; seg++) {
            Vec3 p0 = points[seg];
            Vec3 p1 = points[seg + 1];

            Vec3 segDir = p1.subtract(p0);
            double segLen = segDir.length();
            if (segLen < 0.01) continue;
            // 局部坐标系
            segDir = segDir.normalize();
            Vec3 upLocal = this.getUpVector(segDir);
            Vec3 rightLocal = upLocal.cross(segDir).normalize();

            //    U
            // o1 | o2
            // ---P---- R
            // o4 | 03
            Vec3 o1 = rightLocal.scale(-1).add(upLocal.scale(1)).scale(WIDTH);
            Vec3 o2 = rightLocal.scale(1).add(upLocal.scale(1)).scale(WIDTH);
            Vec3 o3 = rightLocal.scale(1).add(upLocal.scale(-1)).scale(WIDTH);
            Vec3 o4 = rightLocal.scale(-1).add(upLocal.scale(-1)).scale(WIDTH);

            //    c01 - c02
            // c04 + c03 |        p0
            //  |  |  |  |        |
            //  |  |  |  |        |
            //  |  |  |  |        ↓
            //  | c11 + c12       p1
            // c14 - c13
            Vec3 c01 = p0.add(o1); Vec3 c02 = p0.add(o2); Vec3 c03 = p0.add(o3); Vec3 c04 = p0.add(o4);
            Vec3 c11 = p1.add(o1); Vec3 c12 = p1.add(o2); Vec3 c13 = p1.add(o3); Vec3 c14 = p1.add(o4);

            int l0 = lights[seg];
            int l1 = lights[seg + 1];
            // 侧面
            addQuad(consumer, mat, normalMat,
                    c01, c11, c12, c02,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            addQuad(consumer, mat, normalMat,
                    c02, c12, c13, c03,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            addQuad(consumer, mat, normalMat,
                    c03, c13, c14, c04,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            addQuad(consumer, mat, normalMat,
                    c04, c14, c11, c01,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            // 底边
            addQuad(consumer, mat, normalMat,
                    c01, c02, c03, c04,
                    CORE_U_MIN, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MAX,
                    CORE_U_MIN, CORE_V_MAX,
                    l0, l0, l0, l0, overlay);
            addQuad(consumer, mat, normalMat,
                    c11, c14, c13, c12,
                    CORE_U_MIN, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MAX,
                    CORE_U_MIN, CORE_V_MAX,
                    l1, l1, l1, l1, overlay);
        }
    }

    // 绘制矩形
    private void addQuad(VertexConsumer consumer, Matrix4f mat, Matrix3f normalMat,
                         Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4,
                         float u1, float v1, float u2, float v2,
                         float u3, float v3, float u4, float v4,
                         int l1, int l2, int l3, int l4, int overlay) {
        // 计算法线 (近似平均法线)
        Vec3 normal = p2.subtract(p1).cross(p3.subtract(p2)).normalize();
        if (normal.length() < 0.01) normal = new Vec3(0, 1, 0);

        this.addVertex(consumer, mat, normalMat, p1, u1, v1, normal, l1, overlay);
        this.addVertex(consumer, mat, normalMat, p2, u2, v2, normal, l2, overlay);
        this.addVertex(consumer, mat, normalMat, p3, u3, v3, normal, l3, overlay);
        this.addVertex(consumer, mat, normalMat, p4, u4, v4, normal, l4, overlay);
    }

    private void addVertex(VertexConsumer consumer, Matrix4f mat, Matrix3f normalMat,
                           Vec3 pos, float u, float v, Vec3 normal, int light, int overlay) {
        consumer.vertex(mat, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, (float) normal.x, (float) normal.y, (float) normal.z)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull AbstractBioConnectorBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
