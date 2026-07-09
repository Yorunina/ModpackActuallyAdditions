package net.yorunina.maa.mixin;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.block.CropBlock;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HarvesterMovementBehaviour.class)
public class MixinHarvesterMovementBehaviour {

	@Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true, remap = false)
	private void agricraft$onVisitNewPosition(MovementContext context, BlockPos pos, CallbackInfo ci) {
		Level world = context.world;
		if (world.isClientSide) {
			return;
		}

		BlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof CropBlock)) {
			return;
		}

		if (world.getBlockEntity(pos) instanceof AgriCrop crop) {
            crop.harvest(stack -> ((MovementBehaviour) this).dropItem(context, stack), null);
			ci.cancel();
		}
	}

}