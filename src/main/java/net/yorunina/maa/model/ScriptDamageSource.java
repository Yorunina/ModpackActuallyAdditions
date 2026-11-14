package net.yorunina.maa.model;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ScriptDamageSource extends DamageSource implements IDamageSourceData {
    private CompoundTag customData;
    private Map<String, ItemStack> itemData;

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
    public ItemStack getItemData(String key) {
        return itemData == null ? null : itemData.get(key);
    }

    public void setItemData(String key, @Nullable ItemStack stack) {
        if (stack == null) {
            if (itemData != null) itemData.remove(key);
            return;
        }
        if (itemData == null) itemData = new HashMap<>();
        itemData.put(key, stack);
    }

    public void copyItemDataFrom(ScriptDamageSource source) {
        if (source.itemData == null) return;
        source.itemData.forEach((key, stack) -> setItemData(key, stack.copy()));
    }
}
