package net.yorunina.maa.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;

public class RogueHuntPavilionFeature extends Feature<NoneFeatureConfiguration> {
    private static final int PAVILION_Y = 64;
    private static final int SAFE_DIST = 128;
    private static final ResourceLocation SHELL_FORGE_ID = ResourceLocation.fromNamespaceAndPath("playershells", "shell_forge");
    private static final int[][] CORNERS = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

    public RogueHuntPavilionFeature(final Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(final FeaturePlaceContext<NoneFeatureConfiguration> context) {
        final ChunkPos chunkPos = new ChunkPos(context.origin());
        if (!shouldStamp(chunkPos.x, chunkPos.z)) {
            return false;
        }

        final WorldGenLevel level = context.level();
        final BlockPos origin = new BlockPos(chunkPos.x * 16 + 8, PAVILION_Y, chunkPos.z * 16 + 8);
        stampFrame(level, origin);
        stampForge(level, origin);
        stampLanterns(level, origin);
        return true;
    }

    private static boolean shouldStamp(final int chunkX, final int chunkZ) {
        if (!((chunkX == 0 && chunkZ == 0) || (isOdd(chunkX) && isOdd(chunkZ)))) {
            return false;
        }
        final long originX = chunkX * 16L + 8L;
        final long originZ = chunkZ * 16L + 8L;
        return originX * originX + originZ * originZ <= (long) SAFE_DIST * SAFE_DIST;
    }

    private static boolean isOdd(final int value) {
        return Math.floorMod(value, 2) == 1;
    }

    private static void stampFrame(final WorldGenLevel level, final BlockPos origin) {
        final int ox = origin.getX();
        final int oy = origin.getY();
        final int oz = origin.getZ();
        final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
        final BlockState slab = Blocks.SANDSTONE_SLAB.defaultBlockState();

        for (final int[] corner : CORNERS) {
            for (int dy = 0; dy <= 2; dy++) {
                setBlock(level, new BlockPos(ox + corner[0], oy + dy, oz + corner[1]), sandstone);
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                setBlock(level, new BlockPos(ox + dx, oy + 3, oz + dz), slab);
            }
        }
    }

    private static void stampForge(final WorldGenLevel level, final BlockPos origin) {
        final Block forge = ForgeRegistries.BLOCKS.getValue(SHELL_FORGE_ID);
        if (forge == null) {
            return;
        }
        BlockState lower = forge.defaultBlockState();
        if (lower.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            lower = lower.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        }
        if (lower.hasProperty(BlockStateProperties.OPEN)) {
            lower = lower.setValue(BlockStateProperties.OPEN, false);
        }
        BlockState upper = lower;
        if (lower.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            lower = lower.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
            upper = lower.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
        }
        level.setBlock(origin, lower, Block.UPDATE_CLIENTS);
        level.setBlock(origin.above(), upper, Block.UPDATE_ALL);
    }

    private static void stampLanterns(final WorldGenLevel level, final BlockPos origin) {
        final int ox = origin.getX();
        final int oy = origin.getY();
        final int oz = origin.getZ();
        final BlockState standing = Blocks.LANTERN.defaultBlockState().setValue(BlockStateProperties.HANGING, false);
        final BlockState hanging = Blocks.LANTERN.defaultBlockState().setValue(BlockStateProperties.HANGING, true);
        for (final int[] corner : CORNERS) {
            setBlock(level, new BlockPos(ox + corner[0], oy + 4, oz + corner[1]), standing);
        }
        setBlock(level, new BlockPos(ox, oy + 2, oz + 2), hanging);
    }

    private static void setBlock(final WorldGenLevel level, final BlockPos pos, final BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
    }
}
