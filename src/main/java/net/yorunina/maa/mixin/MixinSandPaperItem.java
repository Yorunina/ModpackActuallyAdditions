package net.yorunina.maa.mixin;

import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SandPaperItem.class)
public abstract class MixinSandPaperItem {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"), index = 1)
    private ItemStack fixGhostItem(ItemStack stack) {
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }
}
