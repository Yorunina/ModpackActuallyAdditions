package net.yorunina.maa.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.yorunina.maa.model.ILivingEntityNumberPos;
import net.yorunina.maa.model.ILivingEntityWearingGold;
import net.yorunina.maa.model.MAAEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements ILivingEntityWearingGold, ILivingEntityNumberPos {
    @Unique
    private static final EntityDataAccessor<Boolean> maa$KEEP_OUT_RAIN = MAAEntityData.KEEP_OUT_RAIN;

    @Unique
    private boolean isWearingGold = false;

    @Unique
    private int damageNumberPos = 0;

    @Override
    public boolean isWearingGold() {
        return isWearingGold;
    }

    @Override
    public void setWearingGold(boolean wearingGold) {
        isWearingGold = wearingGold;
    }

    @Unique
    public int getNextNumberPos() {
        return damageNumberPos++;
    }

    @ModifyConstant(method = "hurt", constant = @Constant(intValue = 100), require=0)
    public int lastHurtTimer(int constant) {
        return 1200;
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void maa$defineKeepOutRain(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getEntityData().define(maa$KEEP_OUT_RAIN, false);
    }
}
