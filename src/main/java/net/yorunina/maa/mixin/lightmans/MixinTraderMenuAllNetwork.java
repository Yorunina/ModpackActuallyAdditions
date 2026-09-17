package net.yorunina.maa.mixin.lightmans;

import io.github.lightman314.lightmanscurrency.api.traders.ITraderSource;
import io.github.lightman314.lightmanscurrency.common.menus.TraderMenu;
import io.github.lightman314.lightmanscurrency.common.menus.validation.MenuValidator;
import net.minecraft.world.entity.player.Inventory;
import net.yorunina.maa.compat.lightmans.LightmansTerminalVisibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(value = TraderMenu.TraderMenuAllNetwork.class, remap = false)
public abstract class MixinTraderMenuAllNetwork {
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/github/lightman314/lightmanscurrency/api/traders/ITraderSource;NetworkTraderSource(Z)Ljava/util/function/Supplier;"
            )
    )
    private static Supplier<ITraderSource> maa$filterOpenAll(boolean isClient, int windowID, Inventory inventory, MenuValidator validator) {
        return LightmansTerminalVisibility.networkSource(inventory.player, validator, isClient);
    }
}