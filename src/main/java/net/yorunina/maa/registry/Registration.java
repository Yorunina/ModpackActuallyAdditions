package net.yorunina.maa.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.yorunina.maa.ModpackActuallyAdditions;

import java.util.HashMap;
import java.util.Map;

public class Registration {
    public static final Map<ResourceLocation, StatFormatter> STATS = new HashMap<>();
    public static final ResourceLocation EXCAVATE_TIMES_STAT = registerStats(ModpackActuallyAdditions.id("excavate_times"), StatFormatter.DEFAULT);

    private static ResourceLocation registerStats(ResourceLocation location, StatFormatter formatter) {
        STATS.put(location, formatter);
        return location;
    }

}
