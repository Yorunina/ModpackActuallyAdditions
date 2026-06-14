package net.yorunina.maa.items;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BiomeSelectorItem extends BaseSelectorItem {
    
    public BiomeSelectorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public List<ResourceLocation> getAllEntries() {
        Registry<Biome> registry = getBiomeRegistry();
        if (registry == null) return List.of();
        
        List<ResourceLocation> entries = new ArrayList<>();
        registry.forEach(biome -> {
            ResourceLocation key = registry.getKey(biome);
            if (key != null) entries.add(key);
        });
        entries.sort(Comparator.comparing(ResourceLocation::toString));
        return entries;
    }
    
    private Registry<Biome> getBiomeRegistry() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            return ServerLifecycleHooks.getCurrentServer()
                    .registryAccess().registryOrThrow(Registries.BIOME);
        }
        return getClientBiomeRegistry();
    }
    
    private Registry<Biome> getClientBiomeRegistry() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level != null) {
            return mc.level.registryAccess().registryOrThrow(Registries.BIOME);
        }
        return null;
    }
    
    @Override
    public Component getEntryDisplayName(ResourceLocation location) {
        return Component.translatable("biome." + location.getNamespace() + "." + location.getPath());
    }
    
    @Override
    public Component getSelectorTypeName() {
        return Component.translatable("maa.selector.biome");
    }
}