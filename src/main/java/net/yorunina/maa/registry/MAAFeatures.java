package net.yorunina.maa.registry;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.worldgen.RogueHuntPavilionFeature;

public class MAAFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, ModpackActuallyAdditions.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ROGUE_HUNT_PAVILION =
            FEATURES.register("rogue_hunt_pavilion", () -> new RogueHuntPavilionFeature(NoneFeatureConfiguration.CODEC));
}
