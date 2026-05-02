package net.yorunina.maa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.blocks.forged.hammer.HammerBaseBlockEntity;

@Mixin(HammerBaseBlockEntity.class)
public abstract class MixinHammerBaseBlockEntity {
    @Shadow
    public abstract boolean isFueled();
    @Inject(method = "isFunctional", at = @At("HEAD"), cancellable = true, remap = false)
    private void isFunctional(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.isFueled());
    }
}
