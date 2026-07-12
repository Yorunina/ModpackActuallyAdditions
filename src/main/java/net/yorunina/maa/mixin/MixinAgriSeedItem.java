package net.yorunina.maa.mixin;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.yorunina.maa.registry.MAAStats.CROPS_PLANTED_STAT;

@Mixin(AgriSeedItem.class)
public class MixinAgriSeedItem {
    @Inject(method = "plantSeed", at = @At("HEAD"), remap = false)
    private void onPlantSeed(Player player, AgriCrop crop, ItemStack seed, CallbackInfo ci) {
        if (player != null) {
            player.awardStat(CROPS_PLANTED_STAT);
        }
    }
}