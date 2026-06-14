package net.yorunina.maa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.items.BaseSelectorItem;
import net.yorunina.maa.networks.SyncSelectorEntryMessage;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SelectorClientHandler {

    private static final Map<BaseSelectorItem, Integer> displayTicks = new WeakHashMap<>();
    private static final int DISPLAY_DURATION = 60;
    
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || !mc.options.keyShift.isDown()) return;
        
        ItemStack heldItem = getSelectorItem(mc.player);
        if (heldItem == null) return;
        
        BaseSelectorItem selector = (BaseSelectorItem) heldItem.getItem();
        selector.cycleEntry(heldItem, event.getScrollDelta() > 0);
        
        ResourceLocation selected = selector.getSelectedEntry(heldItem);
        if (selected != null) {
            displayTicks.put(selector, DISPLAY_DURATION);
            new SyncSelectorEntryMessage(getSlotIndex(mc.player, heldItem), isOffHand(mc.player, heldItem), selected).sendToServer();
        }
        
        event.setCanceled(true);
    }
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        
        ItemStack heldItem = getSelectorItem(mc.player);
        if (heldItem == null) return;
        
        BaseSelectorItem selector = (BaseSelectorItem) heldItem.getItem();
        List<ResourceLocation> entries = selector.getAllEntries();
        if (entries.isEmpty()) return;
        
        int ticks = displayTicks.getOrDefault(selector, 0);
        if (ticks > 0) displayTicks.put(selector, ticks - 1);
        
        float alpha = 0.5f + (float) ticks / DISPLAY_DURATION * 0.5f;
        int index = selector.getSelectedIndex(heldItem);
        int total = entries.size();
        
        GuiGraphics gfx = event.getGuiGraphics();
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int y = mc.getWindow().getGuiScaledHeight() - 90;
        
        int color = ((int)(alpha * 255) << 24) | 0xFFFFFF;
        int sideColor = ((int)(alpha * 0.6f * 255) << 24) | 0xFFFFFF;
        
        gfx.drawCenteredString(mc.font, formatEntry(selector, entries.get((index - 1 + total) % total), "§7"), centerX, y - 10, sideColor);
        gfx.drawCenteredString(mc.font, formatCurrent(selector, entries.get(index)), centerX, y + 2, color);
        gfx.drawCenteredString(mc.font, formatEntry(selector, entries.get((index + 1) % total), "§7"), centerX, y + 14, sideColor);
        gfx.drawCenteredString(mc.font, Component.literal((index + 1) + "/" + total), centerX, y + 26, ((int)(alpha * 0.8f * 255) << 24) | 0xFFFFFF);
    }
    
    private static Component formatEntry(BaseSelectorItem selector, ResourceLocation entry, String color) {
        return Component.literal(color).append(selector.getEntryDisplayName(entry));
    }
    
    private static Component formatCurrent(BaseSelectorItem selector, ResourceLocation entry) {
        return Component.literal("§e").append(selector.getSelectorTypeName()).append("§b: ").append(selector.getEntryDisplayName(entry));
    }
    
    private static ItemStack getSelectorItem(LocalPlayer player) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof BaseSelectorItem) return main;
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof BaseSelectorItem) return off;
        return null;
    }
    
    private static int getSlotIndex(LocalPlayer player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (player.getInventory().items.get(i) == stack) return i;
        }
        return player.getInventory().selected;
    }
    
    private static boolean isOffHand(LocalPlayer player, ItemStack stack) {
        return player.getOffhandItem() == stack;
    }
}