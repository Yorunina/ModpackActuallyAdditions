package net.yorunina.maa.mixin;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Window.class)
public class MixinWindow {
    @Unique
    private final List<Integer> shut_up_gl_error$loggedErrorCodes = new ArrayList<>();

    @Inject(
            method = "defaultErrorCallback",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shut_up_gl_error$interceptGlErrorLogging(int p_85383_, long p_85384_, CallbackInfo ci) {
        ci.cancel();
    }
}