package net.yorunina.maa.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseSelectorItem extends Item {
    
    private static final String SELECTED_KEY = "SelectedKey";
    
    private List<ResourceLocation> cachedEntries;
    private Map<ResourceLocation, Integer> entryIndexMap;
    
    public BaseSelectorItem(Properties properties) {
        super(properties);
    }
    
    public abstract List<ResourceLocation> getAllEntries();
    
    public abstract Component getEntryDisplayName(ResourceLocation location);
    
    public abstract Component getSelectorTypeName();
    
    private List<ResourceLocation> getEntries() {
        if (cachedEntries == null) {
            cachedEntries = getAllEntries();
            entryIndexMap = new HashMap<>();
            for (int i = 0; i < cachedEntries.size(); i++) {
                entryIndexMap.put(cachedEntries.get(i), i);
            }
        }
        return cachedEntries;
    }
    
    @Nullable
    public ResourceLocation getSelectedEntry(ItemStack stack) {
        List<ResourceLocation> entries = getEntries();
        if (entries.isEmpty()) return null;
        
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(SELECTED_KEY)) {
            String keyString = tag.getString(SELECTED_KEY);
            try {
                ResourceLocation location = ResourceLocation.parse(keyString);
                if (entryIndexMap.containsKey(location)) {
                    return location;
                }
            } catch (Exception ignored) {
            }
        }
        
        return entries.get(0);
    }
    
    public void setSelectedEntry(ItemStack stack, ResourceLocation location) {
        stack.getOrCreateTag().putString(SELECTED_KEY, location.toString());
    }
    
    public int getSelectedIndex(ItemStack stack) {
        ResourceLocation selected = getSelectedEntry(stack);
        if (selected == null) return 0;
        Integer index = entryIndexMap.get(selected);
        return index != null ? index : 0;
    }
    
    public void cycleEntry(ItemStack stack, boolean forward) {
        List<ResourceLocation> entries = getEntries();
        if (entries.isEmpty()) return;
        
        int currentIndex = getSelectedIndex(stack);
        int newIndex = forward 
                ? (currentIndex + 1) % entries.size()
                : (currentIndex - 1 + entries.size()) % entries.size();
        
        setSelectedEntry(stack, entries.get(newIndex));
    }
    
    public boolean isValidEntry(ResourceLocation location) {
        return entryIndexMap.containsKey(location);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        ResourceLocation selected = getSelectedEntry(stack);
        if (selected != null) {
            tooltip.add(Component.translatable("maa.tooltip.selector.current").append(getEntryDisplayName(selected)));
            tooltip.add(Component.translatable("maa.tooltip.selector.scroll_switch").append(getSelectorTypeName()));
            tooltip.add(Component.translatable("maa.tooltip.selector.right_click_view").append(getSelectorTypeName()));
        } else {
            tooltip.add(Component.translatable("maa.tooltip.selector.not_found").append(getSelectorTypeName()));
        }
        
        tooltip.add(Component.translatable("maa.tooltip.selector.total_count", getEntries().size())
                .append(getSelectorTypeName())
                .append(Component.translatable("maa.tooltip.selector.available")));
    }
    
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}