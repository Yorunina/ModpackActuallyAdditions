package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.item.ItemStack;

public class FTBCustomItemFilterJS extends EventJS {
    public ItemStack filterItem;
    public ItemStack testItem;
    public String id;
    public String[] args;
    public boolean result = false;
    public FTBCustomItemFilterJS(ItemStack filterItem, ItemStack testItem, String id, String[] args) {
        this.filterItem = filterItem;
        this.testItem = testItem;
        this.id = id;
        this.args = args;
    }

    public String getId() {
        return id;
    }
    public String[] getArgs() {
        return args;
    }
    public ItemStack getFilterItem() {
        return filterItem;
    }
    public ItemStack getTestItem() {
        return testItem;
    }
    public boolean getResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
}
