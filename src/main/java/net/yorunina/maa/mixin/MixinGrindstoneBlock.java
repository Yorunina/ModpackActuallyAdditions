package net.yorunina.maa.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(GrindstoneBlock.class)
public class MixinGrindstoneBlock {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"), cancellable = true)
    private void openMenu(BlockState p_53821_, Level p_53822_, BlockPos p_53823_, Player player, InteractionHand pHand, BlockHitResult p_53826_, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack handItem = player.getMainHandItem();
        if (handItem.getItem() instanceof ModularItem modularItem) {
            if (modularItem.canGainHoneProgress(handItem) && !IModularItem.isHoneable(handItem)) cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
