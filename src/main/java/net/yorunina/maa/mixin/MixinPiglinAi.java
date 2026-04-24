package net.yorunina.maa.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.yorunina.maa.model.ILivingEntityWearingGold;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public abstract class MixinPiglinAi {
    @Inject(method = "isWearingGold", at = @At("HEAD"), cancellable = true)
    private static void isWearingGold(LivingEntity pLivingEntity, CallbackInfoReturnable<Boolean> cir) {
        ILivingEntityWearingGold livingEntity = (ILivingEntityWearingGold) pLivingEntity;
        if (livingEntity.isWearingGold()) {
            cir.setReturnValue(true);
        }
    }
}
