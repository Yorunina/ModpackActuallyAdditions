package net.yorunina.maa.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.compat.curios.CuriosCompat;
import se.mickelus.tetra.items.modular.impl.toolbelt.ModularToolbeltItem;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltContainer;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mixin(ToolbeltContainer.class)
public class MixinToolbeltContainer {
    @Shadow
    @Final
    private ItemStack itemStackToolbelt;
    @Unique
    private int toolbeltSlotIndex;
    @Unique
    private boolean isCuriosSlot;
    @Unique
    private String curiosIdentifier;
    @Unique
    private int curiosIndex;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initInject(int windowId, Container playerInventory, ItemStack itemStackToolbelt, Player player, CallbackInfo ci) {
        int slotIndex = -1;
        boolean curiosSlot = false;
        String curiosId = "";
        int curiosIdx = -1;

        if (CuriosCompat.isLoaded) {
            Optional<ImmutableTriple<String, Integer, ItemStack>> maybeToolbelt = CuriosApi.getCuriosHelper().findEquippedCurio(ModularToolbeltItem.instance.get(), player);
            if (maybeToolbelt.isPresent()) {
                curiosSlot = true;
                curiosId = maybeToolbelt.get().left;
                curiosIdx = maybeToolbelt.get().middle;
            }
        }

        if (!curiosSlot) {
            if (ItemStack.isSameItemSameTags(player.getMainHandItem(), itemStackToolbelt)) {
                slotIndex = player.getInventory().selected;
            } else if (ItemStack.isSameItemSameTags(player.getOffhandItem(), itemStackToolbelt)) {
                slotIndex = 40;
            } else {
                for (int i = 0; i < player.getInventory().items.size(); i++) {
                    if (ItemStack.isSameItemSameTags(player.getInventory().items.get(i), itemStackToolbelt)) {
                        slotIndex = i;
                        break;
                    }
                }
            }
        }

        this.toolbeltSlotIndex = slotIndex;
        this.isCuriosSlot = curiosSlot;
        this.curiosIdentifier = curiosId;
        this.curiosIndex = curiosIdx;
    }

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    public void stillValid(Player playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (isCuriosSlot) {
            if (CuriosCompat.isLoaded) {
                Optional<ImmutableTriple<String, Integer, ItemStack>> maybeToolbelt = CuriosApi.getCuriosHelper().findEquippedCurio(ModularToolbeltItem.instance.get(), playerIn);
                if (maybeToolbelt.isPresent()) {
                    ImmutableTriple<String, Integer, ItemStack> toolbeltInfo = maybeToolbelt.get();
                    cir.setReturnValue(toolbeltInfo.left.equals(curiosIdentifier)
                            && toolbeltInfo.middle == curiosIndex
                            && ItemStack.isSameItemSameTags(itemStackToolbelt, toolbeltInfo.right));
                    return;
                }
            }
            cir.setReturnValue(false);
        } else if (toolbeltSlotIndex >= 0) {
            ItemStack currentStack;
            if (toolbeltSlotIndex == 40) {
                currentStack = playerIn.getOffhandItem();
            } else {
                currentStack = playerIn.getInventory().items.get(toolbeltSlotIndex);
            }
            cir.setReturnValue(ItemStack.isSameItemSameTags(itemStackToolbelt, currentStack));
        }
    }
}
