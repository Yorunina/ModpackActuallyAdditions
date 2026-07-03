package net.yorunina.maa.mixin;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.kai_nulled.potioncore.potions.ModPotions;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ModPotions.class)
public class MixinModPotions {
    @ModifyArg(method = "lambda$static$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/Potion;<init>([Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private static MobEffectInstance[] flyPotion(MobEffectInstance[] p_43487_) {
        return new MobEffectInstance[]{new MobEffectInstance(ALObjects.MobEffects.FLYING.get(), 3600, 0)};
    }
    @ModifyArg(method = "lambda$static$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/Potion;<init>([Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private static MobEffectInstance[] longFlyPotion(MobEffectInstance[] p_43487_) {
        return new MobEffectInstance[]{new MobEffectInstance(ALObjects.MobEffects.FLYING.get(), 9800, 0)};
    }
}
