package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.data.VariantData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraVariantKeyFilterItem extends StringValueFilterItem {

    public static class TetraVariantKeyCheck {
        public List<String> slotIds = new ArrayList<>();
        public List<String> keys = new ArrayList<>();
    }

    public static class TetraVariantKeyData extends StringValueData<TetraVariantKeyCheck> {
        public TetraVariantKeyData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected TetraVariantKeyCheck fromString(String s) {

            Pattern pattern = Pattern.compile("^\\[([^\\]]+)\\]\\:\\[([^\\]]+)\\]$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String slots = matcher.group(1);
                String materialKeys = matcher.group(2);
                TetraVariantKeyCheck check = new TetraVariantKeyCheck();
                check.slotIds = List.of(slots.split(","));
                check.keys = List.of(materialKeys.split(","));
                return check;
            }
            return null;
        }

        @Override
        protected String toString(TetraVariantKeyCheck value) {
            if (value == null) return null;
            return "[" + String.join(",", value.slotIds) + "]:" +
                    "[" + String.join(",", value.keys) + "]";
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new TetraVariantKeyData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        TetraVariantKeyData data = getStringValueData(filter);
        TetraVariantKeyCheck check = data.getValue();
        if (check == null) return false;

        for (String slotId : check.slotIds) {
            ItemModule itemModule = modularItem.getModuleFromSlot(stack, slotId);
            VariantData variantData = itemModule.getVariantData(stack);

            if (variantData == null) continue;
            if (check.keys.contains(variantData.key)) return true;
        }
        return false;
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tetra_variant_key";
    }

}