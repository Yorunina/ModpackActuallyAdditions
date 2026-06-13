package net.yorunina.maa.mixin.accessor;

import io.github.lightman314.lightmanscurrency.common.blockentity.trader.ItemTraderBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTraderBlockEntity.class)
public interface ItemTraderBlockEntityAccessor {
    @Accessor("tradeCount")
    int getTradeCount();
}