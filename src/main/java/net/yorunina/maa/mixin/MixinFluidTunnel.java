package net.yorunina.maa.mixin;

import dev.compactmods.machines.tunnel.definitions.FluidTunnel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FluidTunnel.class)
public abstract class MixinFluidTunnel {
    @ModifyConstant(method = "newInstance(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Ldev/compactmods/machines/tunnel/definitions/FluidTunnel$Instance;", constant = @Constant(intValue = 4000), remap = false)
    public int injectNewInstance(int value) {
        return 400000;
    }
}
