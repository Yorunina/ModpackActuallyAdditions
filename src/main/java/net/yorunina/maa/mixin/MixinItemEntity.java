package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.yorunina.maa.MAATags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemEntity.class, priority = 1004)
public abstract class MixinItemEntity {
    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    private void applyTagsImmuneDamages(DamageSource damageSource, float p_32014_, CallbackInfoReturnable<Boolean> cir) {
        if (this.getItem().isEmpty()) return;

        if (this.getItem().is(MAATags.IMMUNE_CACTUS) && damageSource.is(DamageTypes.CACTUS)) {
            cir.setReturnValue(false);
            return;
        }
        if (this.getItem().is(MAATags.IMMUNE_EXPLOSION) && damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(false);
            return;
        }
        if (this.getItem().is(MAATags.IMMUNE_LIGHTNING) && damageSource.is(DamageTypeTags.IS_LIGHTNING)) {
            cir.setReturnValue(false);
            return;
        }
    }

    @ModifyReturnValue(method = "fireImmune", at = @At(value = "RETURN"))
    private boolean applyTagImmuneFire(boolean original) {
        return original || this.getItem().is(MAATags.IMMUNE_FIRE);
    }
}