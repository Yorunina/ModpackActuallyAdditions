package net.yorunina.maa.mixin.accessor;

import net.minecraft.world.effect.MobEffectInstance;
import net.satisfy.brewery.core.item.DrinkBlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrinkBlockItem.class)
public interface DrinkBlockItemAccessor {
    @Invoker("calculateEffectForQuality")
    MobEffectInstance calculateEffect(int quality);
}
