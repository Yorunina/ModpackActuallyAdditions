package net.yorunina.maa.model;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/** A script-created damage source with its own transient data. */
public class ScriptDamageSource extends DamageSource implements IDamageSourceData {
    private CompoundTag customData;

    public ScriptDamageSource(Holder<DamageType> type, @Nullable Entity direct, @Nullable Entity attacker,
                              @Nullable Vec3 position) {
        super(type, direct, attacker, position);
    }

    @Override
    public CompoundTag getCustomData() {
        if (customData == null) customData = new CompoundTag();
        return customData;
    }

    @Override
    public boolean hasCustomData() {
        return customData != null && !customData.isEmpty();
    }
}
