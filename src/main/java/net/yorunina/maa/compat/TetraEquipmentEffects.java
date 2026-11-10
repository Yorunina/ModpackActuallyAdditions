package net.yorunina.maa.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;
import top.theillusivec4.curios.api.CuriosApi;

public final class TetraEquipmentEffects {
    private static final ItemEffect FULL_BLOCK = ItemEffect.get("kubejs:full_block");
    private static final ItemEffect RUNNER = ItemEffect.get("kubejs:runner");

    private TetraEquipmentEffects() {
    }

    public static boolean hasFullBlock(ItemStack stack) {
        return stack.getItem() instanceof IModularItem modular && modular.getEffectLevel(stack, FULL_BLOCK) > 0;
    }

    public static float getUseItemMovementFactor(Player player) {
        int level = CuriosApi.getCuriosInventory(player).map(inventory -> {
            var equipped = inventory.getEquippedCurios();
            int highest = 0;
            for (int slot = 0; slot < equipped.getSlots(); slot++) {
                ItemStack stack = equipped.getStackInSlot(slot);
                if (stack.getItem() instanceof IModularItem modular) {
                    highest = Math.max(highest, modular.getEffectLevel(stack, RUNNER));
                }
            }
            return highest;
        }).orElse(0);
        return Math.min(1.0F, 0.2F + 0.16F * level);
    }
}
