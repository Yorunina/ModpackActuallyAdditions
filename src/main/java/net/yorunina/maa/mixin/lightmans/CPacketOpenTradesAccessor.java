package net.yorunina.maa.mixin.lightmans;

import io.github.lightman314.lightmanscurrency.network.message.trader.CPacketOpenTrades;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketOpenTrades.class)
public interface CPacketOpenTradesAccessor {
    @Accessor("traderID")
    long maa$getTraderID();
}