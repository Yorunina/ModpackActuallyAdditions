package net.yorunina.maa.mixin.accessor;

import io.github.lightman314.lightmanscurrency.api.traders.blockentity.TraderBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TraderBlockEntity.class)
public interface TraderBlockEntityAccessor {
    @Accessor("ignoreCustomTrader")
    boolean isIgnoreCustomTrader();
}
