package net.yorunina.maa.mixin;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSpell.class)
public abstract class MixinAbstractSpell {
    @Inject(method = "requiresLearning", at = @At("HEAD"), remap = false, cancellable = true)
    private void requiresLearning(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
