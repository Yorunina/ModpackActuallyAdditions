package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.data.EffectData;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraEffectLevelFilterItem extends StringValueFilterItem {

    public static class EffectLevelCheck {
        public int mode;
        public ItemEffect effect;
        public int level;
    }

    public static class EffectLevelCheckData extends StringValueData<EffectLevelCheck> {
        public EffectLevelCheckData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected EffectLevelCheck fromString(String s) {
            Pattern pattern = Pattern.compile("^([a-zA-Z:_]+)\\s*([<>=]+)\\s*(\\d+)$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                String effectId = matcher.group(1);
                String operator = matcher.group(2);
                int level = Integer.parseInt(matcher.group(3));

                EffectLevelCheck check = new EffectLevelCheck();

                check.effect = ItemEffect.get(effectId);
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
        protected String toString(EffectLevelCheck value) {
            if (value == null || value.effect == null) {
                return "";
            }
            return value.effect.getKey() +
                    ItemFiltersItems.mode2Operation(value.mode) +
                    value.level;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new EffectLevelCheckData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        EffectLevelCheckData data = getStringValueData(filter);
        EffectLevelCheck check = data.getValue();
        if (check == null || check.effect == null) return false;
        EffectData effectData = modularItem.getEffectData(stack);
        if (!effectData.contains(check.effect)) return false;
        int curLevel = effectData.getLevel(check.effect);
        int requiredLevel = check.level;

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
        return "itemfilters.help_text.tetra_effect_level";
    }

}