package campionebase.erosionera.client.renderer.entity.block;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Set;

public class BioWireRender implements BlockEntityRenderer<AbstractBioConnectorBlockEntity> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID,
            "textures/block/bio_wire.png"
    );
    private static final RenderType WIRE_TYPE = RenderType.entityCutout(TEXTURE);
    private static final double WEIGHT = 10; // 悬链线参数

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

        Set<BlockPos> neighbors = blockEntity.getNeighbors();
        if (neighbors.isEmpty()) return;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 offset = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        for(BlockPos neighborPos : neighbors){
            if (pos.compareTo(neighborPos) > 0) continue;
            if (!(level.getBlockEntity(neighborPos) instanceof AbstractBioConnectorBlockEntity neighbor)) continue;

            AbstractBioConnectorBlockEntity.CachedWireSegments cache = blockEntity.getCachedWireSegments(neighborPos);
            Vec3 start = blockEntity.getWirePos(neighborPos);
            Vec3 end = neighbor.getWirePos(blockEntity.getBlockPos());
            if (cache == null || !cache.isValid) {
                cache = this.buildCache(start.subtract(offset), end.subtract(offset));
                blockEntity.putCachedSegment(neighborPos, cache);
            }

            for (int i = 0; i < cache.points.length; i++) {
                // 注意：getLightColor 需要绝对坐标，因此要加回偏移量
                BlockPos worldPos = BlockPos.containing(
                        cache.points[i].x + offset.x,
                        cache.points[i].y + offset.y,
                        cache.points[i].z + offset.z
                );
                cache.lights[i] = LevelRenderer.getLightColor(level, worldPos);
            }
            this.renderWire(cache, poseStack, bufferSource, packedOverlay);
        }
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

    private AbstractBioConnectorBlockEntity.CachedWireSegments buildCache(Vec3 start, Vec3 end) {
        AbstractBioConnectorBlockEntity.CachedWireSegments cache = new AbstractBioConnectorBlockEntity.CachedWireSegments();

        Vec3 dir = end.subtract(start).normalize();
        Vec3 upLocal = this.getUpVector(dir);
        double totalLen = start.distanceTo(end);

        int count = Math.max(1, (int)(totalLen / 2.0) + 1);
        cache.points = new Vec3[count + 1];
        cache.lights = new int[count + 1];

        for (int i = 0; i <= count; i++) {
            double t = (double) i / count;
            cache.points[i] = getCatenaryPoint(start, dir, upLocal, totalLen, t);
        }
        cache.isValid = true;
        return cache;
    }

    private void renderWire(AbstractBioConnectorBlockEntity.CachedWireSegments cache, PoseStack poseStack, MultiBufferSource bufferSource, int overlay){
        VertexConsumer consumer = bufferSource.getBuffer(WIRE_TYPE);
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normalMat = poseStack.last().normal();

        Vec3[] points = cache.points;
        int[] lights = cache.lights;

        for(int seg = 0; seg < points.length - 1; seg ++){
            Vec3 p0 = points[seg];
            Vec3 p1 = points[seg + 1];

            Vec3 segDir = p1.subtract(p0);
            if (segDir.length() < 0.01) continue;
            // 局部坐标系
            segDir = segDir.normalize();
            Vec3 upLocal = this.getUpVector(segDir);
            Vec3 rightLocal = upLocal.cross(segDir).normalize();

            //    U
            // o1 | o2
            // ---P---- R
            // o4 | 03
            double ux = upLocal.x * WIDTH;
            double uy = upLocal.y * WIDTH;
            double uz = upLocal.z * WIDTH;
            double rx = rightLocal.x * WIDTH;
            double ry = rightLocal.y * WIDTH;
            double rz = rightLocal.z * WIDTH;

            //    c01 - c02
            // c04 + c03 |        p0
            //  |  |  |  |        |
            //  |  |  |  |        |
            //  |  |  |  |        ↓
            //  | c11 + c12       p1
            // c14 - c13
            double c01x = p0.x - rx + ux; double c01y = p0.y - ry + uy; double c01z = p0.z - rz + uz;
            double c02x = p0.x + rx + ux; double c02y = p0.y + ry + uy; double c02z = p0.z + rz + uz;
            double c03x = p0.x + rx - ux; double c03y = p0.y + ry - uy; double c03z = p0.z + rz - uz;
            double c04x = p0.x - rx - ux; double c04y = p0.y - ry - uy; double c04z = p0.z - rz - uz;

            double c11x = p1.x - rx + ux; double c11y = p1.y - ry + uy; double c11z = p1.z - rz + uz;
            double c12x = p1.x + rx + ux; double c12y = p1.y + ry + uy; double c12z = p1.z + rz + uz;
            double c13x = p1.x + rx - ux; double c13y = p1.y + ry - uy; double c13z = p1.z + rz - uz;
            double c14x = p1.x - rx - ux; double c14y = p1.y - ry - uy; double c14z = p1.z - rz - uz;

            int l0 = lights[seg];
            int l1 = lights[seg + 1];
            // 侧面
            this.addQuad(consumer, mat, normalMat,
                    // c01, c11, c12, c02,
                    c01x, c01y, c01z,
                    c11x, c11y, c11z,
                    c12x, c12y, c12z,
                    c02x, c02y, c02z,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            this.addQuad(consumer, mat, normalMat,
                    // c02, c12, c13, c03,
                    c02x, c02y, c02z,
                    c12x, c12y, c12z,
                    c13x, c13y, c13z,
                    c03x, c03y, c03z,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            this.addQuad(consumer, mat, normalMat,
                    // c03, c13, c14, c04,
                    c03x, c03y, c03z,
                    c13x, c13y, c13z,
                    c14x, c14y, c14z,
                    c04x, c04y, c04z,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            this.addQuad(consumer, mat, normalMat,
                    // c04, c14, c11, c01,
                    c04x, c04y, c04z,
                    c14x, c14y, c14z,
                    c11x, c11y, c11z,
                    c01x, c01y, c01z,
                    SHELL_U_MIN, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MIN,
                    SHELL_U_MAX, SHELL_V_MAX,
                    SHELL_U_MIN, SHELL_V_MAX,
                    l0, l1, l1, l0, overlay);
            // 底边
            this.addQuad(consumer, mat, normalMat,
                    // c01, c02, c03, c04,
                    c01x, c01y, c01z,
                    c02x, c02y, c02z,
                    c03x, c03y, c03z,
                    c04x, c04y, c04z,
                    CORE_U_MIN, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MAX,
                    CORE_U_MIN, CORE_V_MAX,
                    l0, l0, l0, l0, overlay);
            this.addQuad(consumer, mat, normalMat,
                    // c11, c14, c13, c12,
                    c11x, c11y, c11z,
                    c14x, c14y, c14z,
                    c13x, c13y, c13z,
                    c12x, c12y, c12z,
                    CORE_U_MIN, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MIN,
                    CORE_U_MAX, CORE_V_MAX,
                    CORE_U_MIN, CORE_V_MAX,
                    l1, l1, l1, l1, overlay);
        }
    }

    // 绘制矩形
    private void addQuad(VertexConsumer consumer, Matrix4f mat, Matrix3f normalMat,
                         double x1, double y1, double z1,
                         double x2, double y2, double z2,
                         double x3, double y3, double z3,
                         double x4, double y4, double z4,
                         float u1, float v1, float u2, float v2,
                         float u3, float v3, float u4, float v4,
                         int l1, int l2, int l3, int l4, int overlay)
    {
        // 计算法线 Vec3 cross
        // Vec3 normal = p2.subtract(p1).cross(p3.subtract(p2)).normalize();
        double ax = x2 - x1, ay = y2 - y1, az = z2 - z1;
        double bx = x3 - x2, by = y3 - y2, bz = z3 - z2;
        double nx = ay * bz - az * by;
        double ny = az * bx - ax * bz;
        double nz = ax * by - ay * bx;

        this.addVertex(consumer, mat, normalMat, x1, y1, z1, u1, v1, nx, ny, nz, l1, overlay);
        this.addVertex(consumer, mat, normalMat, x2, y2, z2, u2, v2, nx, ny, nz, l2, overlay);
        this.addVertex(consumer, mat, normalMat, x3, y3, z3, u3, v3, nx, ny, nz, l3, overlay);
        this.addVertex(consumer, mat, normalMat, x4, y4, z4, u4, v4, nx, ny, nz, l4, overlay);
    }

    private void addVertex(VertexConsumer consumer, Matrix4f mat, Matrix3f normalMat,
                           double x, double y, double z,
                           float u, float v,
                           double nx, double ny, double nz,
                           int light, int overlay) {
        consumer.vertex(mat, (float) x, (float) y, (float) z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, (float) nx, (float) ny, (float) nz)
                .endVertex();
    }
}
