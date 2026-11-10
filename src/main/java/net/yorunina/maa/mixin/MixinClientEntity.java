package net.yorunina.maa.mixin;

import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.world.entity.Entity;
import net.yorunina.maa.client.MAAClientEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinClientEntity {
    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
    private void maa$planarOutlineColor(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        if (!ClientMagicData.getActiveSummons().contains(entity.getUUID()) && MAAClientEffects.isPlanarSightActive()) {
            cir.setReturnValue(0x6c42f5);
        }
    }
}
