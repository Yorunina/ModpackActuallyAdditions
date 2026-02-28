package net.yorunina.maa.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.yorunina.maa.ModpackActuallyAdditions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RandomCubeFeature extends Feature<RandomCubeFeature.Config> {

    public RandomCubeFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (origin.getX() % 32 >= 16 || origin.getZ() % 32 >= 16) return true;
        RandomSource random = context.random();
        Config config = context.config();

        StructureTemplateManager structureManager = level.getLevel().getStructureManager();

        for (int y = 0; y < config.size; y++) {
            BlockPos cubePos = origin.offset(0, y * 32, 0);
            Optional<ResourceLocation> selectedStructure = selectWeightedStructure(config.structures(), random);
            selectedStructure.flatMap(structureManager::get).ifPresent(template -> {
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setRandom(random)
                        .setIgnoreEntities(false)
                        .setFinalizeEntities(true);

                template.placeInWorld(level, cubePos, cubePos, settings, random, 2);
            });
        }
        return true;
    }

    private Optional<ResourceLocation> selectWeightedStructure(List<WeightedStructure> structures, RandomSource random) {
        if (structures.isEmpty()) {
            return Optional.empty();
        }

        int totalWeight = structures.stream().mapToInt(WeightedStructure::weight).sum();
        if (totalWeight <= 0) {
            return Optional.empty();
        }

        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (WeightedStructure structure : structures) {
            currentWeight += structure.weight();
            if (randomValue < currentWeight) {
                return Optional.of(structure.structure());
            }
        }

        return Optional.of(structures.get(structures.size() - 1).structure());
    }

    public record WeightedStructure(ResourceLocation structure, int weight) {
        public static final Codec<WeightedStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("structure").forGetter(WeightedStructure::structure),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("weight").forGetter(WeightedStructure::weight)
        ).apply(instance, WeightedStructure::new));
    }

    public record Config(int size, List<WeightedStructure> structures) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, 64).fieldOf("size").forGetter(Config::size),
                WeightedStructure.CODEC.listOf().fieldOf("structures").forGetter(Config::structures)
        ).apply(instance, Config::new));
    }
}