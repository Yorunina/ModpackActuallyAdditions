package net.yorunina.maa.mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FoodData.class, priority = 1001)
public abstract class MixinFoodData {
    @Unique
    public boolean noAddExhaustion = false;

    @Unique
    public void setNoAddExhaustion(boolean value) {
        this.noAddExhaustion = value;
    }

    @ModifyVariable(
            at = @At("HEAD"),
            ordinal = 0,
            method = {"addExhaustion"},
            argsOnly = true
    )
    public float chestCavityAddExhaustionMixin(float exhaustion) {
        if (this.noAddExhaustion) {
            return 0;
        }
        return exhaustion;
    }
}
