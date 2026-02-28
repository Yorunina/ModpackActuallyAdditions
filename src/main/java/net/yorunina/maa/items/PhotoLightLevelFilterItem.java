package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoLightLevelFilterItem extends StringValueFilterItem {


    public static class LightLevelCheck {
        public int lightLevel;
        public int mode;
    }

    public static class LightLevelData extends StringValueData<LightLevelCheck> {
        public LightLevelData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected LightLevelCheck fromString(String s) {
            LightLevelCheck check = new LightLevelCheck();
            Pattern pattern = Pattern.compile("^([<>=]+)\\s*(\\d+)$");
            Matcher matcher = pattern.matcher(s.trim());
            if (matcher.matches()) {
                String operator = matcher.group(1);
                check.lightLevel = Integer.parseInt(matcher.group(2));
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) {
                    return null;
                }
            }
            return check;
        }

        @Override
        protected String toString(@Nullable LightLevelCheck lightLevelCheck) {
            if (lightLevelCheck == null) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            builder.append(ItemFiltersItems.mode2Operation(lightLevelCheck.mode));
            builder.append(lightLevelCheck.lightLevel);
            return builder.toString();
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new LightLevelData(stack);
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
        if (!extraData.contains("light_level")) return false;

        LightLevelData data = getStringValueData(filter);
        LightLevelCheck lightLevelCheck = data.getValue();

        int lightLevel = extraData.getInt("light_level");

        boolean result = true;
        switch (lightLevelCheck.mode) {
            case 1 -> result &= lightLevel >= lightLevelCheck.lightLevel;
            case 2 -> result &= lightLevel <= lightLevelCheck.lightLevel;
            case 3 -> result &= lightLevel > lightLevelCheck.lightLevel;
            case 4 -> result &= lightLevel < lightLevelCheck.lightLevel;
            case 0 -> result &= lightLevel == lightLevelCheck.lightLevel;
            default -> result = false;
        }

        return result;
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.photo_light_level";
    }
}