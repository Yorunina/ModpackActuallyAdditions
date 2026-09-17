package net.yorunina.maa.mixin.lightmans;

import io.github.lightman314.lightmanscurrency.api.traders.TraderAPI;
import io.github.lightman314.lightmanscurrency.api.traders.TraderData;
import io.github.lightman314.lightmanscurrency.common.menus.TerminalMenu;
import io.github.lightman314.lightmanscurrency.common.menus.validation.MenuValidator;
import io.github.lightman314.lightmanscurrency.network.message.trader.CPacketOpenTrades;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.compat.lightmans.LightmansTerminalVisibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "io.github.lightman314.lightmanscurrency.network.message.trader.CPacketOpenTrades$H", remap = false)
public abstract class MixinCPacketOpenTradesHandler {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void maa$blockHiddenTraders(CPacketOpenTrades message, Player player, CallbackInfo ci) {
        if (!(player.containerMenu instanceof TerminalMenu menu)) {
            return;
        }
        long traderID = ((CPacketOpenTradesAccessor) message).maa$getTraderID();
        if (traderID < 0) {
            return;
        }
        TraderData trader = TraderAPI.getApi().GetTrader(false, traderID);
        if (trader == null) {
            return;
        }
        MenuValidator validator = menu.getValidator();
        if (!LightmansTerminalVisibility.canSee(player, trader, LightmansTerminalVisibility.sourceOf(validator))) {
            ci.cancel();
        }
    }
}