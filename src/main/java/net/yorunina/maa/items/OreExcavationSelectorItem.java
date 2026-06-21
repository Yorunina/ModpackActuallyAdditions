package net.yorunina.maa.items;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.VeinRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

public class OreExcavationSelectorItem extends BaseSelectorItem {

    public static final String REGISTRY_TYPE = "create_ore_excavation";

    public OreExcavationSelectorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public List<ResourceLocation> getAllEntries() {
        RecipeManager rm;
        if(ServerLifecycleHooks.getCurrentServer() != null) {
            rm = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
        } else {
            rm = Minecraft.getInstance().getConnection().getRecipeManager();
        }
        return rm.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()).stream().map(VeinRecipe::getId).toList();
    }
    
    @Override
    public Component getEntryDisplayName(ResourceLocation location) {
        return Component.translatable("create_ore_excavation." + location.getNamespace() + "." + location.getPath());
    }
    
    @Override
    public Component getSelectorTypeName() {
        return Component.translatable("maa.selector.create_ore_excavation");
    }
    
    @Override
    public String getRegistryType() {
        return REGISTRY_TYPE;
    }
}