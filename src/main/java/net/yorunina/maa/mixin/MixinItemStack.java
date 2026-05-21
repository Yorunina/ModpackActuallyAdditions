package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow
    @Nullable
    private CompoundTag tag;

    @Inject(method = "isEnchanted", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/ListTag;"), cancellable = true)
    private void isEnchanted(CallbackInfoReturnable<Boolean> cir) {
        if (this.tag.getBoolean("hideEnchant")) {
            cir.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 2))
    private boolean shouldShowEnchantInTooltip(boolean originaltn) {
        if (this.tag != null && this.tag.getBoolean("hideEnchant")) {
            return false;
        }
        return originaltn;
    }
}
