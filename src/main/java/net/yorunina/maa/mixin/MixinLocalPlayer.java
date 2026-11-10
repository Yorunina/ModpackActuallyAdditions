package net.yorunina.maa.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.yorunina.maa.compat.TetraEquipmentEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.2F),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;sprintTriggerTime:I", opcode = 181, ordinal = 1)),
            require = 2, allow = 2)
    private float maa$useItemMovement(float original) {
        return TetraEquipmentEffects.getUseItemMovementFactor((LocalPlayer) (Object) this);
    }
}
