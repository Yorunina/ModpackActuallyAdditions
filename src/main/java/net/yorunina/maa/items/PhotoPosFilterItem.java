package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class PhotoPosFilterItem extends StringValueFilterItem {
    public static class PosCheck {
        public BlockPos targetPos;
        public int range;
    }

    public static class PosData extends StringValueData<PosCheck> {
        public PosData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected PosCheck fromString(String s) {
            String[] posList = s.split(",");

            if (posList.length != 4) return null;

            PosCheck check = new PosCheck();
            check.targetPos = new BlockPos(Integer.parseInt(posList[0]), Integer.parseInt(posList[1]), Integer.parseInt(posList[2]));
            check.range = Integer.parseInt(posList[3]);
            return check;
        }

        @Override
        protected String toString(@Nullable PosCheck posCheck) {
            if (posCheck == null || posCheck.targetPos == null) {
                return "";
            }
            return posCheck.targetPos.getX() + "," + posCheck.targetPos.getY() + "," + posCheck.targetPos.getZ() + "," + posCheck.range;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new PosData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        Item item = stack.getItem();

        if (!(item instanceof PhotographItem)) return false;
        if (!stack.hasTag()) return false;
        CompoundTag nbt = stack.getTag();
        if (!nbt.contains("photograph_frame")) return false;
        CompoundTag frameNbt = nbt.getCompound("photograph_frame");
        if (!frameNbt.contains("extra_data")) return false;
        CompoundTag extraData = frameNbt.getCompound("extra_data");
        if (!extraData.contains("pos")) return false;

        PosData data = getStringValueData(filter);
        PosCheck check = data.getValue();
        if (check == null) return false;
        ListTag posNbt = extraData.getList("pos", CompoundTag.TAG_DOUBLE);
        BlockPos framePos = new BlockPos((int) posNbt.getDouble(0), (int) posNbt.getDouble(1), (int) posNbt.getDouble(2));
        return check.targetPos.closerThan(framePos, check.range);
    }
    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.exposure_photo_pos";
    }
}