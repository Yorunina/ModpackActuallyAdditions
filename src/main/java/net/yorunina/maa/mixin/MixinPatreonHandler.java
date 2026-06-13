package net.yorunina.maa.mixin;

import io.redspace.ironslib.patreon.PatreonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatreonHandler.class)
public abstract class MixinPatreonHandler {
    @Inject(method = "doDataFetch", at = @At("HEAD"), cancellable = true, remap = false)
    private void doDataFetchInject(CallbackInfo ci) {
        ci.cancel();
    }
}
