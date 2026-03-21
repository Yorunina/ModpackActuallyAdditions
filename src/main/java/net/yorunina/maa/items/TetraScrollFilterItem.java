package net.yorunina.maa.items;

import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.blocks.scroll.ScrollData;
import se.mickelus.tetra.blocks.scroll.ScrollItem;

import javax.annotation.Nullable;

public class TetraScrollFilterItem extends StringValueFilterItem {

    public static class TetraScrollCheck {
        public String key = "";
    }

    public static class TetraScrollData extends StringValueData<TetraScrollCheck> {
        public TetraScrollData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected TetraScrollCheck fromString(String s) {
            TetraScrollCheck check = new TetraScrollCheck();
            check.key = s.trim();
            return check;
        }

        @Override
        protected String toString(TetraScrollCheck value) {
            if (value == null) return null;
            return value.key;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new TetraScrollData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof ScrollItem)) return false;

        TetraScrollData data = getStringValueData(filter);
        TetraScrollCheck check = data.getValue();
        if (check == null) return false;

        ScrollData scrollData = ScrollData.read(stack);
        return scrollData.key.equals(check.key);
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.tetra_scroll";
    }

}