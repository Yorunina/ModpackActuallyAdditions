package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

public class PhotoBiomeFilterItem extends StringValueFilterItem {

    public static class BiomeCheck {
        public String[] biomes;
    }

    public static class BiomeData extends StringValueData<BiomeCheck> {
        public BiomeData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected BiomeCheck fromString(String s) {
            String[] biomeList = s.split(";");

            if (biomeList.length == 0) {
                return null;
            }
            BiomeCheck check = new BiomeCheck();
            check.biomes = biomeList;
            return check;
        }

        @Override
        protected String toString(@Nullable BiomeCheck biomeCheck) {
            if (biomeCheck == null || biomeCheck.biomes == null) {
                return "";
            }
            return String.join(";", biomeCheck.biomes);
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new BiomeData(stack);
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
        if (!extraData.contains("biome")) return false;

        BiomeData data = getStringValueData(filter);
        String[] biomes = data.getValue().biomes;
        if (biomes.length == 0) return false;

        String biome = extraData.getString("biome");

        return Arrays.stream(biomes).anyMatch(biome::startsWith);
    }
    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.exposure_photo_biome";
    }
}