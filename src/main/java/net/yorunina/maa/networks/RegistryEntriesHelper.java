package net.yorunina.maa.networks;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryEntriesHelper {
    private static final Map<String, List<ResourceLocation>> clientCache = new HashMap<>();
    
    public static List<ResourceLocation> getServerEntries(String registryType, ServerPlayer player) {
        if ("structure".equals(registryType)) {
            return getStructureEntries(player);
        } else if ("biome".equals(registryType)) {
            return getBiomeEntries(player);
        }
        return List.of();
    }
    
    private static List<ResourceLocation> getStructureEntries(ServerPlayer player) {
        Registry<Structure> registry = player.getServer().registryAccess()
                .registryOrThrow(Registries.STRUCTURE);
        
        List<ResourceLocation> entries = new ArrayList<>();
        registry.forEach(structure -> {
            ResourceLocation key = registry.getKey(structure);
            if (key != null) entries.add(key);
        });
        entries.sort(Comparator.comparing(ResourceLocation::toString));
        return entries;
    }
    
    private static List<ResourceLocation> getBiomeEntries(ServerPlayer player) {
        Registry<Biome> registry = player.getServer().registryAccess()
                .registryOrThrow(Registries.BIOME);
        
        List<ResourceLocation> entries = new ArrayList<>();
        registry.forEach(biome -> {
            ResourceLocation key = registry.getKey(biome);
            if (key != null) entries.add(key);
        });
        entries.sort(Comparator.comparing(ResourceLocation::toString));
        return entries;
    }
    
    public static void setClientCache(String registryType, List<ResourceLocation> entries) {
        clientCache.put(registryType, entries);
    }
    
    public static List<ResourceLocation> getClientCache(String registryType) {
        return clientCache.getOrDefault(registryType, List.of());
    }
    
    public static boolean hasClientCache(String registryType) {
        return clientCache.containsKey(registryType);
    }
    
    public static void clearClientCache() {
        clientCache.clear();
    }
}