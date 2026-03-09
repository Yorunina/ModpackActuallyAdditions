package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.yorunina.maa.MAATags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract EntityType<?> getType();
    @Shadow
    public abstract void clearFire();

    @Shadow
    public abstract boolean is(Entity p_20356_);

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


    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            ), method = "thunderHit", cancellable = true
    )
    private void thunderHitHurt(ServerLevel p_19927_, LightningBolt p_19928_, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().is(MAATags.IMMUNE_LIGHTNING)) {
                ci.cancel();
            }
            if (itemEntity.getItem().is(MAATags.IMMUNE_FIRE)) {
                clearFire();
            }
        }
    }
}
