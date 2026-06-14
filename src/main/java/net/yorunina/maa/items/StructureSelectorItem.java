package net.yorunina.maa.items;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StructureSelectorItem extends BaseSelectorItem {
    
    public static final String REGISTRY_TYPE = "structure";
    
    public StructureSelectorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public List<ResourceLocation> getAllEntries() {
        Registry<Structure> registry = getStructureRegistry();
        if (registry == null) return List.of();
        
        List<ResourceLocation> entries = new ArrayList<>();
        registry.forEach(structure -> {
            ResourceLocation key = registry.getKey(structure);
            if (key != null) entries.add(key);
        });
        entries.sort(Comparator.comparing(ResourceLocation::toString));
        return entries;
    }
    
    private Registry<Structure> getStructureRegistry() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            return ServerLifecycleHooks.getCurrentServer()
                    .registryAccess().registryOrThrow(Registries.STRUCTURE);
        }
        return getClientStructureRegistry();
    }
    
    private Registry<Structure> getClientStructureRegistry() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level != null) {
            return mc.level.registryAccess().registry(Registries.STRUCTURE).orElse(null);
        }
        return null;
    }
    
    @Override
    public Component getEntryDisplayName(ResourceLocation location) {
        return Component.translatable("structure." + location.getNamespace() + "." + location.getPath());
    }
    
    @Override
    public Component getSelectorTypeName() {
        return Component.translatable("maa.selector.structure");
    }
    
    @Override
    public String getRegistryType() {
        return REGISTRY_TYPE;
    }
}