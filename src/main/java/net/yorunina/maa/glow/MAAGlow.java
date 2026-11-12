package net.yorunina.maa.glow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.model.IEntity;

public final class MAAGlow {
    private MAAGlow() {
    }

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

    public static int getStoredRgb(Entity entity) {
        return entity == null ? 0 : ((IEntity) entity).getGlowRgb();
    }

    private static int nowSecond(Entity entity) {
        return (int) (entity.level().getGameTime() / 20L);
    }
}
