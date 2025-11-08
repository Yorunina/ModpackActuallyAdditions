package net.yorunina.ftbqaa.item;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractComparisonFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MaxDurabilityFilter extends AbstractComparisonFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("max_durability");

    public MaxDurabilityFilter(SmartFilter.Compound parent) {
        this(parent, new NumericComparison(NumericComparison.ComparisonOp.GT, 0, true));
    }

    public MaxDurabilityFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent, comparison);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected int getValueToCompare(ItemStack stack) {
        if (stack.getMaxDamage() != 0) {
            return stack.getMaxDamage();
        } else {
            return 0;
        }
    }


    public static MaxDurabilityFilter fromString(SmartFilter.Compound parent, String str) {
        return new MaxDurabilityFilter(parent, NumericComparison.fromString(str, false));
    }
}