package net.yorunina.maa.mixin;

import com.github.L_Ender.cataclysm.blocks.Altar_Of_Fire_Block;
import com.github.L_Ender.cataclysm.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Altar_Of_Fire_Block.class)
public class MixinAltarOfFireBlock {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.getMainHandItem().is(ModItems.BURNING_ASHES.get())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
