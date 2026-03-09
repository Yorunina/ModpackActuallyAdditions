package net.yorunina.maa.mixin;

import net.minecraft.world.level.Level;
import net.yorunina.maa.compat.kubejs.MAAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {

    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void eternalWinter$modifyRainLevel(CallbackInfoReturnable<Float> cir) {
        if (MAAUtils.INSTANCE.shouldSnowContinuously()) {
            cir.setReturnValue(1.0F);
        }
    }
}