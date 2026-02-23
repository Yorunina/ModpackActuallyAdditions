package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TconToolModifierFilterItem extends StringValueFilterItem {

    public static class ModifierCheck {
        public int mode;
        public ModifierId modifierId;
        public int level;
    }

    public static class ModifierData extends StringValueData<ModifierCheck> {
        public ModifierData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected ModifierCheck fromString(String s) {

            Pattern pattern = Pattern.compile("^([a-zA-Z:_]+)\\s*([<>=]+)\\s*(\\d+)$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String modifierId = matcher.group(1);
                String operator = matcher.group(2);
                int level = Integer.parseInt(matcher.group(3));

                ModifierCheck check = new ModifierCheck();

                if (!modifierId.contains(":")) {
                    modifierId = "tconstruct:" + modifierId;
                }

                check.modifierId = new ModifierId(modifierId);
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) {
                    return null;
                }

                check.level = level;
                return check;
            }
            return null;
        }

        @Override
        protected String toString(ModifierCheck value) {
            if (value == null || value.modifierId == null) {
                return "";
            }

            return value.modifierId +
                    ItemFiltersItems.mode2Operation(value.mode) +
                    value.level;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new ModifierData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        ModifierData data = getStringValueData(filter);

        if (data.getValue() == null || data.getValue().modifierId == null) {
            return false;
        }

        int actualModifierLevel = ModifierUtil.getModifierLevel(stack, data.getValue().modifierId);
        int requiredLevel = data.getValue().level;

        return switch (data.getValue().mode) {
            case 1 -> actualModifierLevel >= requiredLevel;
            case 2 -> actualModifierLevel <= requiredLevel;
            case 3 -> actualModifierLevel > requiredLevel;
            case 4 -> actualModifierLevel < requiredLevel;
            case 0 -> actualModifierLevel == requiredLevel;
            default -> false;
        };
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tcon_tool_modifier";
    }


}