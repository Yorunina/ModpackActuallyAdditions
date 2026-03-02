package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.api.StringValueFilterVariant;
import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.data.ItemProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraPropFilterItem extends StringValueFilterItem {

    public static class TetraPropCheck {
        public int mode;
        public String propType;
        public float num;
    }

    public static class TetraPropData extends StringValueData<TetraPropCheck> {
        public TetraPropData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected TetraPropCheck fromString(String s) {

            Pattern pattern = Pattern.compile("^([a-zA-Z:_]+)\\s*([<>=]+)\\s*(\\d+)$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String propType = matcher.group(1);
                String operator = matcher.group(2);
                TetraPropCheck check = new TetraPropCheck();
                check.propType = propType;
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) return null;

                check.num = Integer.parseInt(matcher.group(3));
                return check;
            }
            return null;
        }

        @Override
        protected String toString(TetraPropCheck value) {
            if (value == null) return null;
            return value.propType + ItemFiltersItems.mode2Operation(value.mode) +
                    value.num;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new TetraPropData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        TetraPropData data = getStringValueData(filter);
        TetraPropCheck check = data.getValue();
        if (check == null) return false;
        ItemProperties itemProp = modularItem.getProperties(stack);
        float cur;
        switch (check.propType) {
            case "integrity":
                cur = itemProp.integrity;
                break;
            case "integrityUsage":
                cur = itemProp.integrityUsage;
                break;
            case "durability":
                cur = itemProp.durability;
                break;
            case "durabilityMultiplier":
                cur = itemProp.durabilityMultiplier;
                break;
            case "integrityMultiplier":
                cur = itemProp.integrityMultiplier;
                break;
            default:
                return false;
        }

        float required = check.num;


        return switch (check.mode) {
            case 1 -> cur >= required;
            case 2 -> cur <= required;
            case 3 -> cur > required;
            case 4 -> cur < required;
            case 0 -> cur == required;
            default -> false;
        };
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<StringValueFilterVariant> getValueVariants(ItemStack stack) {
        List<StringValueFilterVariant> variants = new ArrayList<>();
        List<String> attributeIds = List.of("integrity", "integrityUsage", "durability", "durabilityMultiplier", "integrityMultiplier");

        for (String attributeEntry : attributeIds) {
            attributeIds.add(attributeEntry);
            StringValueFilterVariant variant = new StringValueFilterVariant(attributeEntry);
            variant.title = Component.literal(attributeEntry);
            variant.icon = Items.BOOK.getDefaultInstance();
            variants.add(variant);
        }
        return variants;
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tetra_prop";
    }

}