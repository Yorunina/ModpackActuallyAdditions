package net.yorunina.maa.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.yorunina.maa.networks.RegistryEntriesHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseSelectorItem extends Item {
    
    private static final String SELECTED_KEY = "SelectedKey";
    
    private List<ResourceLocation> cachedEntries;
    
    public BaseSelectorItem(Properties properties) {
        super(properties);
    }
    
    public abstract List<ResourceLocation> getAllEntries();
    
    public abstract Component getEntryDisplayName(ResourceLocation location);
    
    public abstract Component getSelectorTypeName();
    
    public abstract String getRegistryType();
    
    public List<ResourceLocation> getEntriesForDisplay() {
        List<ResourceLocation> clientCache = RegistryEntriesHelper.getClientCache(getRegistryType());
        if (!clientCache.isEmpty()) {
            return clientCache;
        }
        return getAllEntries();
    }
    
    private List<ResourceLocation> getEntries() {
        if (cachedEntries == null) {
            cachedEntries = getAllEntries();
        }
        return cachedEntries;
    }
    
    @Nullable
    public ResourceLocation getSelectedEntry(ItemStack stack) {
        List<ResourceLocation> entries = getEntriesForDisplay();
        if (entries.isEmpty()) return null;
        
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(SELECTED_KEY)) {
            String keyString = tag.getString(SELECTED_KEY);
            try {
                ResourceLocation location = ResourceLocation.parse(keyString);
                if (entries.contains(location)) {
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
    
    public boolean isValidEntry(ResourceLocation location) {
        return getEntries().contains(location);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        ResourceLocation selected = getSelectedEntry(stack);
        if (selected != null) {
            tooltip.add(Component.translatable("maa.tooltip.selector.current").append(getEntryDisplayName(selected)));
            tooltip.add(Component.translatable("maa.tooltip.selector.right_click_view").append(getSelectorTypeName()));
        } else {
            tooltip.add(Component.translatable("maa.tooltip.selector.not_found").append(getSelectorTypeName()));
        }
        
        tooltip.add(Component.translatable("maa.tooltip.selector.total_count", getEntriesForDisplay().size())
                .append(getSelectorTypeName())
                .append(Component.translatable("maa.tooltip.selector.available")));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            openSelectorScreenClient(player, stack, hand);
        }
        return InteractionResultHolder.success(stack);
    }

    private void openSelectorScreenClient(Player player, ItemStack stack, InteractionHand hand) {
        net.yorunina.maa.client.SelectorScreenHandler.openScreen(this, stack, hand);
    }
}