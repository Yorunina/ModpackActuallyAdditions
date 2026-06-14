package net.yorunina.maa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.yorunina.maa.client.screens.SelectorScreen;
import net.yorunina.maa.items.BaseSelectorItem;
import net.yorunina.maa.networks.RegistryEntriesHelper;
import net.yorunina.maa.networks.RequestRegistryEntriesMessage;

public class SelectorScreenHandler {
    
    private static BaseSelectorItem pendingSelectorItem = null;
    private static ItemStack pendingStack = null;
    private static int pendingSlotIndex = 0;
    private static boolean pendingIsOffHand = false;
    
    public static void openScreen(BaseSelectorItem selectorItem, ItemStack stack, InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        int slotIndex = hand == InteractionHand.OFF_HAND ? -1 : mc.player.getInventory().selected;
        boolean isOffHand = hand == InteractionHand.OFF_HAND;
        
        String registryType = selectorItem.getRegistryType();
        
        if (RegistryEntriesHelper.hasClientCache(registryType)) {
            mc.setScreen(new SelectorScreen(selectorItem, stack, slotIndex, isOffHand));
        } else if (mc.getConnection() != null) {
            pendingSelectorItem = selectorItem;
            pendingStack = stack;
            pendingSlotIndex = slotIndex;
            pendingIsOffHand = isOffHand;
            new RequestRegistryEntriesMessage(registryType).sendToServer();
        } else {
            mc.setScreen(new SelectorScreen(selectorItem, stack, slotIndex, isOffHand));
        }
    }
    
    public static void onRegistryEntriesReceived(String registryType) {
        if (pendingSelectorItem != null && pendingSelectorItem.getRegistryType().equals(registryType)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.setScreen(new SelectorScreen(pendingSelectorItem, pendingStack, pendingSlotIndex, pendingIsOffHand));
            }
            clearPending();
        }
    }
    
    private static void clearPending() {
        pendingSelectorItem = null;
        pendingStack = null;
        pendingSlotIndex = 0;
        pendingIsOffHand = false;
    }
}