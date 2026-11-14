package net.yorunina.maa.mixin;

import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.world.entity.Entity;
import net.yorunina.maa.client.MAAClientEffects;
import net.yorunina.maa.glow.MAAGlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinClientEntity {
    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
    private void maa$planarOutlineColor(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        // 优先级：显式描边色 > 灵视紫 > 原版队伍色
        int glow = MAAGlow.getActiveRgb(entity);
        if (glow != 0) {
            cir.setReturnValue(glow);
            return;
        }
        if (!ClientMagicData.getActiveSummons().contains(entity.getUUID()) && MAAClientEffects.isPlanarSightActive()) {
            cir.setReturnValue(0x6c42f5);
        }
    }
}
