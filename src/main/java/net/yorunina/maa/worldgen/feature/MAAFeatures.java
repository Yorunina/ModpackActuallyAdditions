package net.yorunina.maa.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.yorunina.maa.ModpackActuallyAdditions;

public class MAAFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = 
            DeferredRegister.create(Registries.FEATURE, ModpackActuallyAdditions.MODID);

    public static final RegistryObject<Feature<RandomCubeFeature.Config>> RANDOM_CUBE_FEATURE = 
            FEATURES.register("random_cube_feature", 
                    () -> new RandomCubeFeature(RandomCubeFeature.Config.CODEC));
}