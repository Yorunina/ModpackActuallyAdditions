package net.yorunina.ftbqaa.utils;

import com.google.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureLoader {
    private static final Map<String, List<StructureTemplate.StructureBlockInfo>> structureRepository = Maps.newHashMap();

    @Nullable
    public static List<StructureTemplate.StructureBlockInfo> get(String name) {
        return structureRepository.computeIfAbsent(name, (key) -> {
            try {
                return loadStructure(key);
            } catch (IOException e) {
                return null;
            }
        });
    }

    private static List<StructureTemplate.StructureBlockInfo> loadStructure(String name) throws IOException {
        if (name.contains("..")) {
            throw new IOException();
        } else {
            CompoundTag CompoundTag = NbtIo.read(new File("config/ftbqaa/quests_structures/" + name + ".nbt"));
            if (!CompoundTag.contains("DataVersion", 99)) {
                CompoundTag.putInt("DataVersion", 500);
            }

            return loadPalette(CompoundTag.getList("palette", 10), CompoundTag.getList("blocks", 10), -CompoundTag.getList("size", 3).getInt(0) / 2);
        }
    }

    public static Rotation getRotation(Direction dir) {
        if (dir == Direction.NORTH) {
            return Rotation.CLOCKWISE_180;
        } else if (dir == Direction.WEST) {
            return Rotation.CLOCKWISE_90;
        } else {
            return dir == Direction.EAST ? Rotation.COUNTERCLOCKWISE_90 : Rotation.NONE;
        }
    }

    public static boolean isValidStructure(Level world, BlockPos base, Rotation rot, List<StructureTemplate.StructureBlockInfo> template, boolean ignoreState) {
        for (StructureTemplate.StructureBlockInfo info : template) {
            BlockPos pos = base.offset(info.pos().offset(0, 0, 1).rotate(rot));
            BlockState state = world.getBlockState(pos);
            BlockState state2 = info.state().rotate(rot);
            if (ignoreState) {
                if (state.getBlock() != state2.getBlock()) {
                    return false;
                }
            } else if (!state.equals(state2)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidState(BlockState state, BlockState state2, boolean ignoreState) {
        if (ignoreState) {
            return state.getBlock() == state2.getBlock();
        } else {
            return state.equals(state2);
        }
    }

    private static List<StructureTemplate.StructureBlockInfo> loadPalette(ListTag palette, ListTag states, int offsetX) {
        BasicPalette template$basicpalette = new BasicPalette();

        for (int i = 0; i < palette.size(); ++i) {
            template$basicpalette.addMapping(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), palette.getCompound(i)), i);
        }

        List<StructureTemplate.StructureBlockInfo> blocks = new ArrayList();

        for (int j = 0; j < states.size(); ++j) {
            CompoundTag CompoundTag = states.getCompound(j);
            ListTag ListTag = CompoundTag.getList("pos", 3);
            BlockPos blockpos = new BlockPos(ListTag.getInt(0) + offsetX, ListTag.getInt(1), ListTag.getInt(2));
            BlockState blockstate = template$basicpalette.stateFor(CompoundTag.getInt("state"));
            CompoundTag CompoundTag1 = null;
            if (CompoundTag.contains("nbt")) {
                CompoundTag1 = CompoundTag.getCompound("nbt");
                CompoundTag1.putInt("x", blockpos.getX());
                CompoundTag1.putInt("y", blockpos.getY());
                CompoundTag1.putInt("z", blockpos.getZ());
            }

            StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(blockpos, blockstate, CompoundTag1);
            blocks.add(info);
        }

        Comparator<StructureTemplate.StructureBlockInfo> comparator = Comparator
                .comparingInt((StructureTemplate.StructureBlockInfo infox) -> infox.pos().getX())
                .thenComparingInt(infox -> infox.pos().getY())
                .thenComparingInt(infox -> infox.pos().getZ());
        blocks.sort(comparator);
        return blocks;
    }

    static class BasicPalette implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE;
        private final IdMapper<BlockState> ids = new IdMapper(16);

        private BasicPalette() {
        }

        @Nullable
        public BlockState stateFor(int id) {
            BlockState blockstate = this.ids.byId(id);
            return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
        }

        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(BlockState state, int id) {
            this.ids.addMapping(state, id);
        }

        static {
            DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        }
    }
}
