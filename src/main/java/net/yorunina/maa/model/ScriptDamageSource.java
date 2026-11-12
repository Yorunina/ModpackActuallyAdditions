package net.yorunina.maa.model;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ScriptDamageSource extends DamageSource implements IDamageSourceData {
    private CompoundTag customData;
    private Map<String, Object> objectData;

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

    @Nullable
    public Object getObjectData(String key) {
        return objectData == null ? null : objectData.get(key);
    }

    public void setObjectData(String key, @Nullable Object value) {
        if (value == null) {
            if (objectData != null) objectData.remove(key);
            return;
        }
        if (objectData == null) objectData = new HashMap<>();
        objectData.put(key, value);
    }

    public void copyObjectDataFrom(ScriptDamageSource source) {
        if (source.objectData == null || source.objectData.isEmpty()) return;
        if (objectData == null) objectData = new HashMap<>();
        objectData.putAll(source.objectData);
    }
}
