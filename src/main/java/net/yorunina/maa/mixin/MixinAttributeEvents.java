package net.yorunina.maa.mixin;

import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.compat.kubejs.MAAEvents;
import net.yorunina.maa.compat.kubejs.events.AttributesLibDodgeEventJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttributeEvents.class, remap = false)
public abstract class MixinAttributeEvents {
    @Inject(method = "onDodge", at = @At("TAIL"))
    private void maa$attributesLibDodge(LivingEntity entity, CallbackInfo ci) {
        if (!entity.level().isClientSide() && MAAEvents.ATTRIBUTES_LIB_DODGE.hasListeners()) {
            MAAEvents.ATTRIBUTES_LIB_DODGE.post(new AttributesLibDodgeEventJS(entity));
        }
    }
}
