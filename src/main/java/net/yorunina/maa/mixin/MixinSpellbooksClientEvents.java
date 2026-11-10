package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import net.yorunina.maa.client.MAAClientEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEvents.class)
public abstract class MixinSpellbooksClientEvents {
    @ModifyExpressionValue(method = "changeFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", remap = true), remap = false)
    private static boolean maa$planarFogColor(boolean original) {
        return original || MAAClientEffects.isPlanarSightActive();
    }
}
