package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import net.mehvahdjukaar.amendments.common.recipe.FluidAndItemCraftResult;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CauldronCraftEventJS extends LevelEventJS {

    private final Level level;
    private final List<ItemStack> items;
    private final boolean boiling;
    private final int tankCapacity;
    private final SoftFluidStack fluidStack;
    private FluidAndItemCraftResult craftResult;



    public CauldronCraftEventJS(Level level, boolean boiling, int tankCapacity, SoftFluidStack fluidStack, List<ItemStack> items) {
        super();
        this.level = level;
        this.items = items;
        this.boiling = boiling;
        this.tankCapacity = tankCapacity;
        this.fluidStack = fluidStack;
        this.craftResult = null;
    }


    @Override
    public Level getLevel() {
        return level;
    }

    public List<ItemStack> getItems() {
        return items;
    }
    public boolean isBoiling() {
        return boiling;
    }
    public int getTankCapacity() {
        return tankCapacity;
    }
    public SoftFluidStack getFluidStack() {
        return fluidStack;
    }

    public FluidAndItemCraftResult getCraftResult() {
        return craftResult;
    }
    public void setCraftResult(FluidAndItemCraftResult craftResult) {
        this.craftResult = craftResult;
    }
}

