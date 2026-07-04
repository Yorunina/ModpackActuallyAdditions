package net.yorunina.maa.compat.jei;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilVariant;
import com.agricraft.agricraft.common.util.LangUtils;
import com.agricraft.agricraft.common.util.Platform;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.yorunina.maa.ModpackActuallyAdditions;

import java.util.ArrayList;
import java.util.List;


public class AgriSoilInfoCategory implements IRecipeCategory<AgriSoil> {

    public static final ResourceLocation ID = ModpackActuallyAdditions.id("agri_soil");
    public static final RecipeType<AgriSoil> TYPE = RecipeType.create(ModpackActuallyAdditions.MODID, "agri_soil", AgriSoil.class);

    private static final int WIDTH = 162;
    private static final int HEIGHT = 84;
    private static final int MAX_SLOTS = 8;

    private final IDrawable background;
    private final IDrawable icon;

    public AgriSoilInfoCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        Item iconItem = getMagnifyingGlassItem();
        if (iconItem == null) {
            iconItem = net.minecraft.world.item.Items.FARMLAND;
        }
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
    }

    @Override
    public RecipeType<AgriSoil> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("maa.jei.category.agri_soil");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AgriSoil soil, IFocusGroup focuses) {
        NonNullList<ItemStack> stacks = collectSoilStacks(soil);
        int count = Math.min(stacks.size(), MAX_SLOTS);
        int startX = (WIDTH - count * 18) / 2;
        for (int i = 0; i < count; i++) {
            builder.addInputSlot(startX + i * 18, 4)
                    .addItemStack(stacks.get(i));
        }
    }

    @Override
    public void draw(AgriSoil soil, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int textColor = 0x404040;
        int valueColor = 0x202020;
        int y = 28;
        int labelX = 8;

        String soilId = AgriApi.getSoilId(soil).map(ResourceLocation::toString).orElse("");
        Component soilName = LangUtils.soilName(soilId);
        MutableComponent soilLine = Component.translatable("agricraft.tooltip.magnifying.soil.soil").append(soilName);
        graphics.drawString(minecraft.font, soilLine, labelX, y, textColor, false);
        y += 13;

        graphics.drawString(minecraft.font,
                Component.translatable("agricraft.tooltip.magnifying.soil.humidity")
                        .append(LangUtils.soilPropertyName("humidity", soil.humidity())),
                labelX, y, valueColor, false);
        y += 11;

        graphics.drawString(minecraft.font,
                Component.translatable("agricraft.tooltip.magnifying.soil.acidity")
                        .append(LangUtils.soilPropertyName("acidity", soil.acidity())),
                labelX, y, valueColor, false);
        y += 11;

        graphics.drawString(minecraft.font,
                Component.translatable("agricraft.tooltip.magnifying.soil.nutrients")
                        .append(LangUtils.soilPropertyName("nutrients", soil.nutrients())),
                labelX, y, valueColor, false);
        y += 11;

        double growthModifier = soil.growthModifier() == null ? 1.0 : soil.growthModifier();
        String modifierText = formatModifier(growthModifier);
        MutableComponent growthLine = Component.translatable("maa.jei.agri_soil.growth_modifier", modifierText);
        graphics.drawString(minecraft.font, growthLine, labelX, y, valueColor, false);
    }

    private static String formatModifier(double value) {
        String text = String.format("%.2f", value);
        if (value > 1.0) {
            text = ChatFormatting.GREEN + text;
        } else if (value < 1.0) {
            text = ChatFormatting.RED + text;
        }
        return text;
    }

    private static NonNullList<ItemStack> collectSoilStacks(AgriSoil soil) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        Platform platform = Platform.get();
        if (platform == null) {
            return stacks;
        }
        for (AgriSoilVariant variant : soil.variants()) {
            for (Item item : platform.getItemsFromLocation(variant.block())) {
                ItemStack stack = new ItemStack(item);
                if (stacks.stream().noneMatch(existing -> existing.getItem() == item)) {
                    stacks.add(stack);
                }
            }
        }
        return stacks;
    }

    private static Item getMagnifyingGlassItem() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("agricraft", "magnifying_glass");
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            return null;
        }
        return item;
    }

    public static void registerCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AgriSoilInfoCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        AgriApi.getSoilRegistry().ifPresent(registry -> {
            List<AgriSoil> soils = new ArrayList<>();
            for (AgriSoil soil : registry) {
                soils.add(soil);
            }
            if (!soils.isEmpty()) {
                registration.addRecipes(TYPE, soils);
            }
        });
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
        Item magnifyingGlass = getMagnifyingGlassItem();
        if (magnifyingGlass != null) {
            registration.addRecipeCatalysts(TYPE, new ItemStack(magnifyingGlass));
        }
    }
}
