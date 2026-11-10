package net.yorunina.maa.mixin;

import io.redspace.ironsspellbooks.render.GlowingEyesLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.client.MAAClientEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlowingEyesLayer.class)
public abstract class MixinPlanarSightRendering {
    @Inject(method = "getEyeType", at = @At("RETURN"), cancellable = true, remap = false)
    private static void maa$planarEyes(LivingEntity entity, CallbackInfoReturnable<GlowingEyesLayer.EyeType> cir) {
        if (cir.getReturnValue() == GlowingEyesLayer.EyeType.None
                && entity == Minecraft.getInstance().player && MAAClientEffects.isPlanarSightActive()) {
            cir.setReturnValue(GlowingEyesLayer.EyeType.Planar_Sight);
        }
    }

    @Inject(method = "getEyeScale", at = @At("RETURN"), cancellable = true, remap = false)
    private static void maa$planarEyeScale(LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValue() == GlowingEyesLayer.EyeType.None.scale
                && entity == Minecraft.getInstance().player && MAAClientEffects.isPlanarSightActive()) {
            cir.setReturnValue(GlowingEyesLayer.EyeType.Planar_Sight.scale);
        }
    }
}
