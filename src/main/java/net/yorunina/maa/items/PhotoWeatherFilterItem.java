package net.yorunina.maa.items;

import dev.ftb.mods.ftbteams.api.Team;
import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import io.github.mortuusars.exposure.world.item.PhotographItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

public class PhotoWeatherFilterItem extends StringValueFilterItem {

    public static class WeatherCheck {
        public String[] weathers;
    }

    public static class WeatherData extends StringValueData<WeatherCheck> {
        public WeatherData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected WeatherCheck fromString(String s) {
            String[] weatherList = s.split(";");

            if (weatherList.length == 0) {
                return null;
            }
            WeatherCheck check = new WeatherCheck();
            check.weathers = weatherList;
            return check;
        }

        @Override
        protected String toString(@Nullable WeatherCheck weatherCheck) {
            if (weatherCheck == null || weatherCheck.weathers == null) {
                return "";
            }
            return String.join(";", weatherCheck.weathers);
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new WeatherData(stack);
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
        if (!extraData.contains("weather")) return false;

        WeatherData data = getStringValueData(filter);
        String[] weathers = data.getValue().weathers;
        if (weathers.length == 0) return false;

        String weather = extraData.getString("weather");
        // 只要前缀匹配即认为匹配
        return Arrays.stream(weathers).anyMatch(weather::startsWith);
    }
    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.exposure_photo_biome";
    }
}