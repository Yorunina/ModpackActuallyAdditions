package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import io.github.mortuusars.exposure.item.PhotographItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoDayTimeFilterItem extends StringValueFilterItem {


    public static class DayTimeCheck {
        public int dayTime;
        public int mode;
    }

    public static class DayTimeData extends StringValueData<DayTimeCheck> {
        public DayTimeData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected DayTimeCheck fromString(String s) {
            DayTimeCheck check = new DayTimeCheck();
            Pattern pattern = Pattern.compile("^([<>=]+)\\s*(\\d+)$");
            Matcher matcher = pattern.matcher(s.trim());
            if (matcher.matches()) {
                String operator = matcher.group(1);
                check.dayTime = Integer.parseInt(matcher.group(2));
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) {
                    return null;
                }
            }
            return check;
        }

        @Override
        protected String toString(@Nullable DayTimeCheck dayTimeCheck) {
            if (dayTimeCheck == null) {
                return "";
            }
            return ItemFiltersItems.mode2Operation(dayTimeCheck.mode) +
                    dayTimeCheck.dayTime;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new DayTimeData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        Item item = stack.getItem();

        if (!(item instanceof PhotographItem)) return false;
        if (!stack.hasTag()) return false;
        CompoundTag nbt = stack.getTag();
        if (!nbt.contains("DayTime")) return false;

        DayTimeData data = getStringValueData(filter);
        DayTimeCheck dayTimeCheck = data.getValue();

        int dayTime = nbt.getInt("DayTime");

        boolean result = true;
        switch (dayTimeCheck.mode) {
            case 1 -> result &= dayTime >= dayTimeCheck.dayTime;
            case 2 -> result &= dayTime <= dayTimeCheck.dayTime;
            case 3 -> result &= dayTime > dayTimeCheck.dayTime;
            case 4 -> result &= dayTime < dayTimeCheck.dayTime;
            case 0 -> result &= dayTime == dayTimeCheck.dayTime;
            default -> result = false;
        }

        return result;
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.photo_day_time";
    }
}