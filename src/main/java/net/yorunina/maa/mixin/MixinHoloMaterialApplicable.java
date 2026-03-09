package net.yorunina.maa.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.schematic.HoloMaterialApplicable;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

import java.util.List;

@Mixin(value = HoloMaterialApplicable.class, remap = false)
public class MixinHoloMaterialApplicable {
    @Shadow
    private List<Component> tooltip;

    @Shadow
    private IModularItem item;

    @Shadow
    private ItemStack itemStack;

    @Shadow
    private String slot;

    @Shadow
    private UpgradeSchematic schematic;

    @Inject(method = "update", at =
    @At(value = "INVOKE",
            target = "Lse/mickelus/tetra/items/modular/impl/holo/ModularHolosphereItem;findHolosphere(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/item/ItemStack;",
            shift = At.Shift.AFTER),
            cancellable = true)
    private void update(Level level, BlockPos pos, WorkbenchTile blockEntity, ItemStack itemStack, String slot, UpgradeSchematic schematic, Player playerEntity, CallbackInfo ci) {
        this.tooltip.add(Component.translatable("tetra.holo.craft.holosphere_shortcut"));
        this.item = (IModularItem) itemStack.getItem();
        this.itemStack = itemStack;
        this.slot = slot;
        this.schematic = schematic;
        ci.cancel();
    }
}
