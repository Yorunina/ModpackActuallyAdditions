package net.yorunina.maa.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yorunina.maa.ModpackActuallyAdditions;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemFiltersItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModpackActuallyAdditions.MODID);
    public static final RegistryObject<Item> DURABILITY_ITEM_FILTER = ITEMS.register("durability", DurabilityFilterItem::new);
    public static final RegistryObject<Item> TETRA_EFFECT_LEVEL_FILTER = ITEMS.register("tetra_effect_level",
            TetraEffectLevelFilterItem::new);
    public static final RegistryObject<Item> TETRA_EFFECT_EFFICIENCY_FILTER = ITEMS.register("tetra_effect_efficiency",
            TetraEffectEfficiencyFilterItem::new);
    public static final RegistryObject<Item> TETRA_IMPROVEMENT_LEVEL_FILTER = ITEMS.register("tetra_improvement_level",
            TetraImprovementFilterItem::new);
    public static final RegistryObject<Item> TETRA_PROP_FILTER = ITEMS.register("tetra_prop",
            TetraPropFilterItem::new);
    public static final RegistryObject<Item> ITEM_ATTRIBUTE_FILTER = ITEMS.register("item_attribute",
            ItemAttributeFilterItem::new);
    public static final RegistryObject<Item> TETRA_MODULE_FILTER = ITEMS.register("tetra_module",
            TetraModuleFilterItem::new);
    public static final RegistryObject<Item> TETRA_VARIANT_KEY_FILTER = ITEMS.register("tetra_variant_key",
            TetraVariantKeyFilterItem::new);
    public static final RegistryObject<Item> KUBE_CUSTOM_FILTER = ITEMS.register("kube_custom",
            KubeCustomFilterItem::new);
    public static final RegistryObject<Item> TETRA_TOOL_ACTION_FILTER = ITEMS.register("tetra_tool_action",
            TetraToolActionFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_STRUCTURE_FILTER = ITEMS.register("exposure_photo_structure",
            PhotoStructureFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_ENTITY_FILTER = ITEMS.register("exposure_photo_entity",
            PhotoEntityFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_BIOME_FILTER = ITEMS.register("exposure_photo_biome",
            PhotoBiomeFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_WEATHER_FILTER = ITEMS.register("exposure_photo_weather",
            PhotoWeatherFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_LIGHT_LEVEL_FILTER = ITEMS.register("exposure_photo_light_level",
            PhotoLightLevelFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_DAY_TIME_FILTER = ITEMS.register("exposure_photo_day_time",
            PhotoDayTimeFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_DIMENSION_FILTER = ITEMS.register("exposure_photo_dimension",
            PhotoDimensionFilterItem::new);
    public static final RegistryObject<Item> EXPOSURE_PHOTO_POS_FILTER = ITEMS.register("exposure_photo_pos",
            PhotoPosFilterItem::new);
    public static final RegistryObject<Item> CHEST_CAVITY_ORGAN_SCORE_FILTER = ITEMS.register("chest_cavity_organ_score",
            ChestCavityOrganScoreFilterItem::new);
    public static final RegistryObject<Item> TETRA_SCROLL_FILTER = ITEMS.register("tetra_scroll",
            TetraScrollFilterItem::new);



    public static int operation2Mode(String operator) {
        return switch (operator) {
            case ">=" -> 1;
            case "<=" -> 2;
            case ">" -> 3;
            case "<" -> 4;
            case "=", "==" -> 0;
            default -> -1;
        };
    }

    public static String mode2Operation(int mode) {
        return switch (mode) {
            case 1 -> ">=";
            case 2 -> "<=";
            case 3 -> ">";
            case 4 -> "<";
            case 0 -> "==";
            default -> "";
        };
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
