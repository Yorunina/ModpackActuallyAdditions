package net.yorunina.maa.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.tom.createores.block.entity.DrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.model.IDrillBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static net.yorunina.maa.registry.Registration.EXCAVATE_TIMES_STAT;

@Mixin(DrillBlockEntity.class)
public abstract class MixinDrillBlockEntity extends SmartBlockEntity implements IDrillBlock {
    @Unique
    private UUID owner;

    public MixinDrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lcom/tom/createores/util/QueueInventory;load(Lnet/minecraft/nbt/ListTag;)V"), remap = false)
    protected void read(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("owner")) {
            this.owner = tag.getUUID("owner");
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;"))
    protected void write(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        if (this.owner != null) {
            tag.putUUID("owner", this.owner);
        }
    }

    @Inject(method = "onFinished", at = @At("HEAD"), remap = false)
    protected void onFinished(CallbackInfo ci) {
        Player player = getOwner(level);
        if (player != null) {
            player.awardStat(EXCAVATE_TIMES_STAT);
        }
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Player getOwner(Level level) {
        if (this.owner == null) return null;
        return level.getPlayerByUUID(owner);
    }
}
