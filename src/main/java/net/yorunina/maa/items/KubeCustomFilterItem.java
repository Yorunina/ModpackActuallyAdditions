package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import net.yorunina.maa.compat.kubejs.events.FTBCustomItemFilterJS;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.yorunina.maa.compat.kubejs.MAAEvents.FTB_CUSTOM_ITEM_FILTER;

public class KubeCustomFilterItem extends StringValueFilterItem {

    public static class TetraPropCheck {
        public String id;
        public String[] customArgs;
    }

    public static class TetraPropData extends StringValueData<TetraPropCheck> {
        public TetraPropData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected TetraPropCheck fromString(String s) {

            Pattern pattern = Pattern.compile("^([^:]+):\\[(.*)\\]$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String id = matcher.group(1);
                String[] customArgs = matcher.groupCount() > 1 ? matcher.group(2).split(",") : new String[0];
                TetraPropCheck check = new TetraPropCheck();
                check.id = id;
                check.customArgs = customArgs;
                return check;
            }
            return null;
        }

        @Override
        protected String toString(TetraPropCheck value) {
            if (value == null) return null;
            return value.id + ":" + "[" +
                    String.join(",", value.customArgs) + "]";
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new TetraPropData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;
        TetraPropData data = getStringValueData(filter);
        TetraPropCheck check = data.getValue();
        if (check == null) return false;
        FTBCustomItemFilterJS event = new FTBCustomItemFilterJS(filter, stack, check.id, check.customArgs);
        FTB_CUSTOM_ITEM_FILTER.post(event, check.id);
        return event.getResult();
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.kube_custom";
    }

}