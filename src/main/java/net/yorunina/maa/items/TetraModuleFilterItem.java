package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.data.ItemProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraModuleFilterItem extends StringValueFilterItem {

    public static class TetraModuleCheck {
        public List<String> slotIds = new ArrayList<>();
        public List<String> moduleKeys = new ArrayList<>();
    }

    public static class TetraModuleData extends StringValueData<TetraModuleCheck> {
        public TetraModuleData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected TetraModuleCheck fromString(String s) {

            Pattern pattern = Pattern.compile("^\\[([^\\]]+)\\]\\:\\[([^\\]]+)\\]$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String slots = matcher.group(1);
                String moduleKeys = matcher.group(2);
                TetraModuleCheck check = new TetraModuleCheck();
                check.slotIds = List.of(slots.split(","));
                check.moduleKeys = List.of(moduleKeys.split(","));
                return check;
            }
            return null;
        }

        @Override
        protected String toString(TetraModuleCheck value) {
            if (value == null) return null;
            return "[" + String.join(",", value.slotIds) + "]:" +
                    "[" + String.join(",", value.moduleKeys) + "]";
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new TetraModuleData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        TetraModuleData data = getStringValueData(filter);
        TetraModuleCheck check = data.getValue();
        if (check == null) return false;

        for (String slotId : check.slotIds) {
            ItemModule itemModule = modularItem.getModuleFromSlot(stack, slotId);
            if (itemModule == null) continue;
            if (check.moduleKeys.contains(itemModule.getKey())) return true;
        }
        return false;
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tetra_module";
    }

}