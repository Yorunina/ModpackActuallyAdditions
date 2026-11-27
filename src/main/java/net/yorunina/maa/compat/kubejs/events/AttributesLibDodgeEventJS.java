package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import net.minecraft.world.entity.LivingEntity;

public class AttributesLibDodgeEventJS extends LivingEntityEventJS {
    private final LivingEntity entity;

    public AttributesLibDodgeEventJS(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }
}
