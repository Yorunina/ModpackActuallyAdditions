package net.yorunina.maa.mixin;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreakingHeartBlockEntity.class)
public abstract class MixinCreakingHeartBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity {
    public MixinCreakingHeartBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "clearCreakingInfo", at = @At("HEAD"), cancellable = true, remap = false)
    private void onClearCreakingInfo(CallbackInfo ci) {
        if (this.level instanceof VirtualRenderWorld) {
            ci.cancel();
        }
    }
}