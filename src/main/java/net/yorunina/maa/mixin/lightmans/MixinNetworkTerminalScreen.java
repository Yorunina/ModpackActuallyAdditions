package net.yorunina.maa.mixin.lightmans;

import io.github.lightman314.lightmanscurrency.api.traders.TraderAPI;
import io.github.lightman314.lightmanscurrency.api.traders.TraderData;
import io.github.lightman314.lightmanscurrency.client.gui.screen.NetworkTerminalScreen;
import io.github.lightman314.lightmanscurrency.common.menus.TerminalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.compat.lightmans.LightmansTerminalVisibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = NetworkTerminalScreen.class, remap = false)
public abstract class MixinNetworkTerminalScreen {
    @Redirect(
            method = "traderList",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/github/lightman314/lightmanscurrency/api/traders/TraderAPI;GetAllNetworkTraders(Z)Ljava/util/List;"
            )
    )
    private List<TraderData> maa$filterTerminalList(TraderAPI api, boolean isClient) {
        List<TraderData> traders = api.GetAllNetworkTraders(isClient);
        NetworkTerminalScreen screen = (NetworkTerminalScreen) (Object) this;
        TerminalMenu menu = screen.getMenu();
        Player player = Minecraft.getInstance().player;
        return LightmansTerminalVisibility.filter(traders, player, LightmansTerminalVisibility.sourceOf(menu.getValidator()));
    }
}