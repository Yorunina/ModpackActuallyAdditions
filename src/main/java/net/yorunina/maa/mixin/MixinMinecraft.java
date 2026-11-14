package net.yorunina.maa.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.client.MAAClientEffects;
import net.yorunina.maa.glow.MAAGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Inject(method = "shouldEntityAppearGlowing", at = @At("RETURN"), cancellable = true)
    private void maa$planarOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (MAAGlow.getActiveRgb(entity) != 0) {
            cir.setReturnValue(true);
            return;
        }
        var player = Minecraft.getInstance().player;
        if (!cir.getReturnValue() && entity instanceof LivingEntity && player != null
                && Math.abs((float) (entity.getY() - player.getY())) < 18.0F
                && MAAClientEffects.isPlanarSightActive()) {
            cir.setReturnValue(true);
        }
    }
}
