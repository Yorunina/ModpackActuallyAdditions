package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Unique
    public boolean noFireDamage = false;
    @Unique
    public void setNoFireDamage(boolean value) {
        this.noFireDamage = value;
    }
    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean baseTickHut(Entity instance, DamageSource p_19946_, float p_19947_, Operation<Boolean> original) {
        if (noFireDamage) return true;
        return original.call(instance, p_19946_, p_19947_);
    }
}
