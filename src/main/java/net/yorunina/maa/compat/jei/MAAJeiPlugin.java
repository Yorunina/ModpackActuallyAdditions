package net.yorunina.maa.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.yorunina.maa.ModpackActuallyAdditions;

@JeiPlugin
public class MAAJeiPlugin implements IModPlugin {

    private static final String AGRICRAFT_MODID = "agricraft";

    @Override
    public ResourceLocation getPluginUid() {
        return ModpackActuallyAdditions.id("main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!isAgriCraftLoaded()) {
            return;
        }
        AgriSoilInfoCategory.registerCategory(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!isAgriCraftLoaded()) {
            return;
        }
        AgriSoilInfoCategory.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (!isAgriCraftLoaded()) {
            return;
        }
        AgriSoilInfoCategory.registerCatalysts(registration);
    }

    private static boolean isAgriCraftLoaded() {
        return ModList.get().isLoaded(AGRICRAFT_MODID);
    }
}
