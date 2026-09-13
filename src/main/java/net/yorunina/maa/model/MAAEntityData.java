package net.yorunina.maa.model;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;

public final class MAAEntityData {
    public static final EntityDataAccessor<Boolean> KEEP_OUT_RAIN =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    private MAAEntityData() {
    }
}
