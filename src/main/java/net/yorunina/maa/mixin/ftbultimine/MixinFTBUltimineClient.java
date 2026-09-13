package net.yorunina.maa.mixin.ftbultimine;

import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FTBUltimineClient.class, remap = false)
public abstract class MixinFTBUltimineClient {
    @Shadow
    private boolean canUltimine;

    @Inject(method = "renderGameOverlay", at = @At("HEAD"), cancellable = true)
    private void maa$hideOverlayWhenDisabled(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        if (!this.canUltimine) {
            ci.cancel();
        }
    }
}