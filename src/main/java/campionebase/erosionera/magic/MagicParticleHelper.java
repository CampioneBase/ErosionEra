package campionebase.erosionera.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class MagicParticleHelper {
    private MagicParticleHelper() { throw new UnsupportedOperationException(); }

    /** 工作台工作时候的粒子效果 */
    public static void spawnRuneCraftingParticle(Level level, BlockPos pos){
        // 获取台阶上表面中心坐标
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 1.0;
        double centerZ = pos.getZ() + 0.5;

        if (level instanceof ServerLevel serverLevel){
            // 从四个方向生成飞向中心的粒子
            for (int i = 0; i < 4; i++) {
                double angle = (i * Math.PI / 2) + (level.getGameTime() * 0.1);
                double radius = 1.5; // 吸取半径

                double startX = centerX + radius * Math.cos(angle);
                double startZ = centerZ + radius * Math.sin(angle);
                double startY = centerY + 0.5; // 从稍高的位置开始

                // 创建飞向中心的粒子轨迹
                for (int j = 0; j < 3; j++) {
                    double progress = j * 0.3;
                    double particleX = startX + (centerX - startX) * progress;
                    double particleZ = startZ + (centerZ - startZ) * progress;
                    double particleY = startY + (centerY - startY) * progress * 0.5;

                    serverLevel.sendParticles(
                            ParticleTypes.ENCHANT, // 附魔粒子
                            particleX, particleY, particleZ,
                            1, // 数量
                            0, 0, 0, // 偏移
                            0.0 // 速度
                    );
                }
            }

            // 中心聚集旋转效果
            for (int i = 0; i < 8; i++) {
                double angle = (i * Math.PI / 4) + (level.getGameTime() * 0.2);
                double radius = 0.3;

                double particleX = centerX + radius * Math.cos(angle);
                double particleZ = centerZ + radius * Math.sin(angle);

                serverLevel.sendParticles(
                        ParticleTypes.WITCH, // 女巫粒子（紫色）
                        particleX, centerY, particleZ,
                        1,
                        0.02, 0.02, 0.02,
                        0.01
                );
            }

            // 中心充能爆发
            if (level.getGameTime() % 20 == 0) { // 每秒钟一次爆发
                for (int i = 0; i < 12; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double radius = 0.1 + Math.random() * 0.2;

                    serverLevel.sendParticles(
                            ParticleTypes.DRAGON_BREATH, // 龙息粒子
                            centerX + radius * Math.cos(angle),
                            centerY,
                            centerZ + radius * Math.sin(angle),
                            2,
                            0.05, 0.05, 0.05,
                            0.03
                    );
                }
            }
        }
    }
}
