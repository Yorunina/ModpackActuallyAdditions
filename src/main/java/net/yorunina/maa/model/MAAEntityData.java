package net.yorunina.maa.model;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;

public final class MAAEntityData {
    public static final EntityDataAccessor<Boolean> KEEP_OUT_RAIN =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    /** 描边颜色（RGB，0 表示不发光）。 */
    public static final EntityDataAccessor<Integer> GLOW_RGB =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    /** 描边到期时刻，单位是世界游戏时间秒；0 表示永久。 */
    public static final EntityDataAccessor<Integer> GLOW_END =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    private MAAEntityData() {
    }
}
