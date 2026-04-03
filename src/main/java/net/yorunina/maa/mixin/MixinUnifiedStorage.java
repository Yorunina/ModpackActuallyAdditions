package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.dimensionnet.UnifiedStorage;
import com.wintercogs.beyonddimensions.api.storage.key.IStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import net.yorunina.maa.model.IDimensionNet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnifiedStorage.class)
public abstract class MixinUnifiedStorage {
    @Shadow(remap = false)
    @Final
    private DimensionsNet net;

    @Inject(method = "insert(Lcom/wintercogs/beyonddimensions/api/storage/key/IStackKey;JZ)Lcom/wintercogs/beyonddimensions/api/storage/key/KeyAmount;",
            at = @At(value = "INVOKE", target = "Lcom/wintercogs/beyonddimensions/api/dimensionnet/helper/UnifiedStorageBeforeInsertHandler;onBeforeInsert(Lcom/wintercogs/beyonddimensions/api/storage/key/KeyAmount;Lcom/wintercogs/beyonddimensions/api/dimensionnet/DimensionsNet;)Lcom/wintercogs/beyonddimensions/api/dimensionnet/helper/UnifiedStorageBeforeInsertHandler$BeforeInsertHandlerReturnInfo;"), cancellable = true, remap = false)
    public void insertLock(IStackKey<?> key, long amount, boolean simulate, CallbackInfoReturnable<KeyAmount> cir, @Local(name = "input") KeyAmount input) {
        if (((IDimensionNet)net).isLocked()) {
            cir.setReturnValue(input);
        }
    }
}
