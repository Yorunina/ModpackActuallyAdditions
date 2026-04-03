package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import net.minecraft.nbt.CompoundTag;
import net.yorunina.maa.model.IDimensionNet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionsNet.class)
public abstract class MixinDimensionsNet implements IDimensionNet {
    @Unique
    public boolean locked;

    @Unique
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Unique
    public boolean isLocked() {
        return this.locked;
    }

    @Inject(method = "save", at = @At(value = "TAIL"))
    public void serializeNBT(CallbackInfoReturnable<CompoundTag> cir, @Local(name = "tag") CompoundTag tag) {
        tag.putBoolean("locked", this.locked);
    }

    @Inject(method = "load", at = @At(value = "TAIL"), remap = false)
    private static void load(CompoundTag tag, CallbackInfoReturnable<DimensionsNet> cir, @Local(name = "net") DimensionsNet net) {
        MixinDimensionsNet mixinNet = (MixinDimensionsNet) (Object) net;
        mixinNet.locked = tag.getBoolean("locked");
    }
}
