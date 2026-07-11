package net.yorunina.maa.mixin;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.stream.Stream;

@Mixin(CropBlockEntity.class)
public abstract class MixinCropBlockEntity extends BlockEntity {
    @Shadow
    public abstract boolean hasPlant();

    @Shadow
    protected abstract void executePlantGrowthTick();

    @Shadow
    public abstract Stream<AgriCrop> streamNeighbours();

    public MixinCropBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Unique
    public void modpackActuallyAdditions$forceApplyGrowthTick() {
        if (this.level != null && !this.level.isClientSide()) {
            if (this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.DOUBLE_STICKS) {
                AgriApi.getMutationHandler().getActiveCrossBreedEngine().handleCrossBreedTick((AgriCrop) this, this.streamNeighbours(), this.level.random);
            } else {
                if (!this.hasPlant()) return;
                this.executePlantGrowthTick();
            }
        }
    }
}
