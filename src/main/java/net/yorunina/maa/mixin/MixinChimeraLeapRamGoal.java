package net.yorunina.maa.mixin;

import com.hollingsworth.arsnouveau.common.entity.goal.chimera.ChimeraLeapRamGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChimeraLeapRamGoal.class)
public abstract class MixinChimeraLeapRamGoal {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common/entity/goal/chimera/ChimeraLeapRamGoal;endRam()V", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void tickInject(CallbackInfo ci) {
        ci.cancel();
    }
}