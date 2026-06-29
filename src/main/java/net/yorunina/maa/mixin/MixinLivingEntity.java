package net.yorunina.maa.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.model.ILivingEntityWearingGold;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements ILivingEntityWearingGold {
    @Unique
    private boolean isWearingGold = false;

    @Override
    public boolean isWearingGold() {
        return isWearingGold;
    }

    @Override
    public void setWearingGold(boolean wearingGold) {
        isWearingGold = wearingGold;
    }
}
