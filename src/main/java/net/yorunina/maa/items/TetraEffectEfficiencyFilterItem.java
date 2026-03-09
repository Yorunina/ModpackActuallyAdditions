package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.data.EffectData;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraEffectEfficiencyFilterItem extends StringValueFilterItem {

    public static class EffectEfficiencyCheck {
        public int mode;
        public ItemEffect effect;
        public float efficiency;
    }

    public static class EffectEfficiencyCheckData extends StringValueData<EffectEfficiencyCheck> {
        public EffectEfficiencyCheckData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected EffectEfficiencyCheck fromString(String s) {
            Pattern pattern = Pattern.compile("^([a-zA-Z:_]+)\\s*([<>=]+)\\s*([\\d\\.]+)$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String effectId = matcher.group(1);
                String operator = matcher.group(2);
                float efficiency = Float.parseFloat(matcher.group(3));

                EffectEfficiencyCheck check = new EffectEfficiencyCheck();

                check.effect = ItemEffect.get(effectId);
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) {
                    return null;
                }

                check.efficiency = efficiency;
                return check;
            }
            return null;
        }

        @Override
        protected String toString(EffectEfficiencyCheck value) {
            if (value == null || value.effect == null) {
                return "";
            }

            return value.effect.getKey() +
                    ItemFiltersItems.mode2Operation(value.mode) +
                    value.efficiency;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new EffectEfficiencyCheckData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        EffectEfficiencyCheckData data = getStringValueData(filter);
        EffectEfficiencyCheck check = data.getValue();
        if (check == null || check.effect == null) return false;
        EffectData effectData = modularItem.getEffectData(stack);
        if (!effectData.contains(check.effect)) return false;
        float curLevel = effectData.getEfficiency(check.effect);
        float requiredLevel = check.efficiency;

        return switch (check.mode) {
            case 1 -> curLevel >= requiredLevel;
            case 2 -> curLevel <= requiredLevel;
            case 3 -> curLevel > requiredLevel;
            case 4 -> curLevel < requiredLevel;
            case 0 -> curLevel == requiredLevel;
            default -> false;
        };
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tetra_effect_efficiency";
    }

}