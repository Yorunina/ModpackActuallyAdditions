package net.yorunina.maa.mixin;

import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.entity.StorageBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageBlockEntity.class)
public abstract class MixinBreweryStorageBlockEntity extends BlockEntity {
    public MixinBreweryStorageBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "setChanged", at = @At(value = "INVOKE", target = "Lnet/satisfy/brewery/core/block/entity/StorageBlockEntity;getUpdatePacket()Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;"))
    public void setChanged(CallbackInfo ci) {
        if (this.level instanceof SchematicLevel schematicLevel) {
            this.level = schematicLevel.getLevel();
        }
    }
}
