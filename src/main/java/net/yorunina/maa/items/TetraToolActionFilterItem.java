package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.data.ToolData;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraToolActionFilterItem extends StringValueFilterItem {

    public static class TetraToolActionCheck {
        public int mode;
        public ToolAction toolAction;
        public int num;
    }

    public static class TetraToolActionData extends StringValueData<TetraToolActionCheck> {
        public TetraToolActionData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected TetraToolActionCheck fromString(String s) {

            Pattern pattern = Pattern.compile("^([a-zA-Z:_]+)\\s*([<>=]+)\\s*([\\d]+)$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String actionType = matcher.group(1);
                String operator = matcher.group(2);
                TetraToolActionCheck check = new TetraToolActionCheck();
                check.toolAction = ToolAction.get(actionType);
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) return null;

                check.num = Integer.parseInt(matcher.group(3));
                return check;
            }
            return null;
        }

        @Override
        protected String toString(TetraToolActionCheck value) {
            if (value == null) return null;
            return value.toolAction.name() + ItemFiltersItems.mode2Operation(value.mode) +
                    value.num;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new TetraToolActionData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        TetraToolActionData data = getStringValueData(filter);
        TetraToolActionCheck check = data.getValue();
        if (check == null) return false;
        ToolData toolData = modularItem.getToolData(stack);
        if (toolData == null) return false;
        int cur = toolData.getLevel(check.toolAction);

        int required = check.num;
        return switch (check.mode) {
            case 1 -> cur >= required;
            case 2 -> cur <= required;
            case 3 -> cur > required;
            case 4 -> cur < required;
            case 0 -> cur == required;
            default -> false;
        };
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tetra_tool_action";
    }

}