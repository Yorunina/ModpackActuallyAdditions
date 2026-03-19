package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.module.data.ImprovementData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetraImprovementFilterItem extends StringValueFilterItem {

    public static class ImprovementLevelCheck {
        public int mode;
        public String improvement;
        public int level;
    }

    public static class ImprovementLevelCheckData extends StringValueData<ImprovementLevelCheck> {
        public ImprovementLevelCheckData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected ImprovementLevelCheck fromString(String s) {
            Pattern pattern = Pattern.compile("^([a-zA-Z:_]+)\\s*([<>=]+)\\s*(\\d+)$");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                ImprovementLevelCheck check = new ImprovementLevelCheck();
                String improvement = matcher.group(1);
                String operator = matcher.group(2);
                check.level = Integer.parseInt(matcher.group(3));
                check.improvement = improvement;
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) return null;

                return check;
            }
            return null;
        }

        @Override
        protected String toString(ImprovementLevelCheck value) {
            if (value == null || value.improvement == null) {
                return "";
            }

            return value.improvement +
                    ItemFiltersItems.mode2Operation(value.mode) +
                    value.level;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new ImprovementLevelCheckData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof ModularItem modularItem)) return false;
        ImprovementLevelCheckData data = getStringValueData(filter);
        ImprovementLevelCheck check = data.getValue();
        if (check == null || check.improvement == null) return false;

        AtomicInteger curLevelAtom = new AtomicInteger();
        Arrays.stream(modularItem.getMajorModuleKeys(stack))
                .map(moduleKey -> modularItem.getModuleFromSlot(stack, moduleKey))
                .forEach(module -> {
                    if (module instanceof ItemModuleMajor majorModule) {
                        curLevelAtom.addAndGet(majorModule.getImprovementLevel(stack, check.improvement));
                    }
                });

        int curLevel = curLevelAtom.get();
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
        return "itemfilters.help_text.tetra_improvement_level";
    }

}