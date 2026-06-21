package net.yorunina.maa.mixin.accessor;

import dev.compactmods.machines.tunnel.definitions.ItemTunnel;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTunnel.Instance.class)
public interface ItemTunnelInstanceAccessor {
    @Accessor("handler")
    ItemStackHandler getHandler();
}
