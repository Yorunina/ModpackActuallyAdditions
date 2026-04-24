package net.yorunina.maa.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.compat.kubejs.events.VillagerUpdateSpecialPrices;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.yorunina.maa.compat.kubejs.MAAEvents.VILLAGER_UPDATE_SPECIAL_PRICES;

@Mixin(Villager.class)
public abstract class MixinVillager {
    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void updateSpecialPrices(Player player, CallbackInfo ci) {
        VILLAGER_UPDATE_SPECIAL_PRICES.post(new VillagerUpdateSpecialPrices(player, (Villager) (Object) this));
    }
}
