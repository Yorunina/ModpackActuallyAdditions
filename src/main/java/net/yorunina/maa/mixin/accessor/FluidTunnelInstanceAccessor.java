package net.yorunina.maa.mixin.accessor;

import dev.compactmods.machines.tunnel.definitions.FluidTunnel;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidTunnel.Instance.class)
public interface FluidTunnelInstanceAccessor {
    @Accessor("handler")
    FluidTank getHandler();
}
