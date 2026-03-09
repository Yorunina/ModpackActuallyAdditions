package net.yorunina.maa.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.yorunina.maa.compat.kubejs.MAAUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class MixinBiome {

    @Inject(method = "getPrecipitationAt", at = @At("HEAD"), cancellable = true)
    private void eternalWinter$getPrecipitationType(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (MAAUtils.INSTANCE.shouldSnowContinuously()) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        }
    }


    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
    private void eternalWinter$shouldSnow(LevelReader p_47520_, BlockPos p_47521_, CallbackInfoReturnable<Boolean> cir) {
        if (MAAUtils.INSTANCE.shouldSnowContinuously()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTemperature", at = @At("HEAD"), cancellable = true)
    private void eternalWinter$getTemperature(BlockPos p_47506_, CallbackInfoReturnable<Float> cir) {
        if (MAAUtils.INSTANCE.shouldSnowContinuously()) {
            cir.setReturnValue(MAAUtils.INSTANCE.getGlobalTemperature());
        }
    }
}