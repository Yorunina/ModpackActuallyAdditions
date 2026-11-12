package net.yorunina.maa.glow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.model.IEntity;

/**
 * 实体描边颜色的统一入口。
 *
 * <p>状态存放在实体的同步数据里（见 {@link net.yorunina.maa.model.MAAEntityData}），
 * 由原版 EntityData 负责服务端到客户端的同步；NBT 只做存档快照，读写都在
 * {@link net.yorunina.maa.mixin.MixinEntity} 里完成。热路径上不要读写 NBT。</p>
 *
 * <p>颜色为 24 位 RGB，{@code 0} 表示不发光；到期时刻用世界游戏时间秒表示，
 * {@code 0} 表示永久。</p>
 */
public final class MAAGlow {
    private MAAGlow() {
    }

    /**
     * 让实体带指定颜色描边。
     *
     * @param rgb     24 位 RGB，0 等同 {@link #clear(Entity)}
     * @param seconds 持续秒数，&lt;= 0 表示永久
     */
    public static void set(Entity entity, int rgb, int seconds) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        int color = rgb & 0xFFFFFF;
        if (color == 0) {
            clear(entity);
            return;
        }
        int end = seconds <= 0 ? 0 : nowSecond(entity) + seconds;
        // 先写到期时间再写颜色，客户端不会看到"新颜色 + 旧到期"的组合
        ((IEntity) entity).setGlowEnd(end);
        ((IEntity) entity).setGlowRgb(color);
    }

    public static void clear(Entity entity) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        ((IEntity) entity).setGlowRgb(0);
        ((IEntity) entity).setGlowEnd(0);
    }

    /** 当前有效颜色；已过期或未设置返回 0。 */
    public static int getActiveRgb(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return 0;
        }
        int rgb = ((IEntity) entity).getGlowRgb();
        if (rgb == 0) {
            return 0;
        }
        int end = ((IEntity) entity).getGlowEnd();
        return end == 0 || nowSecond(entity) < end ? rgb : 0;
    }

    /** 不做过期判断的原始颜色，供脚本查询。 */
    public static int getStoredRgb(Entity entity) {
        return entity == null ? 0 : ((IEntity) entity).getGlowRgb();
    }

    private static int nowSecond(Entity entity) {
        return (int) (entity.level().getGameTime() / 20L);
    }
}
