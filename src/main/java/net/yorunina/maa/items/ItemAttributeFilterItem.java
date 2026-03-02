package net.yorunina.maa.items;

import com.google.common.collect.Multimap;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.latvian.mods.itemfilters.api.StringValueFilterVariant;
import dev.latvian.mods.itemfilters.item.StringValueData;
import dev.latvian.mods.itemfilters.item.StringValueFilterItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemAttributeFilterItem extends StringValueFilterItem {

    public static class AttributeCheck {
        public int mode; // 0: ==, 1: >=, 2: <=, 3: >, 4: <
        public Attribute attribute;
        public double num;
    }

    public static class AttributeData extends StringValueData<AttributeCheck> {
        public AttributeData(ItemStack is) {
            super(is);
        }

        @Nullable
        @Override
        protected AttributeCheck fromString(String s) {

            Pattern pattern = Pattern.compile("([a-zA-Z:_]+)\\s*([<>=]+)\\s*([\\d\\.]+)");
            Matcher matcher = pattern.matcher(s.trim());

            if (matcher.matches()) {
                AttributeCheck check = new AttributeCheck();
                String attributeIdStr = matcher.group(1);
                String operator = matcher.group(2);
                check.num = Double.parseDouble(matcher.group(3));
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(attributeIdStr));
                if (attribute == null) return null;
                check.attribute = attribute;
                check.mode = ItemFiltersItems.operation2Mode(operator);
                if (check.mode == -1) return null;
                return check;
            }
            return null; // No match
        }

        @Override
        protected String toString(AttributeCheck value) {
            if (value == null || value.attribute == null) {
                return "";
            }
            ResourceLocation attributeId = ForgeRegistries.ATTRIBUTES.getKey(value.attribute);
            if (attributeId == null) return "";

            return attributeId + ItemFiltersItems.mode2Operation(value.mode) + value.num;
        }
    }

    @Override
    public StringValueData<?> createData(ItemStack stack) {
        return new AttributeData(stack);
    }

    @Override
    public boolean filter(ItemStack filter, ItemStack stack) {
        if (stack.isEmpty()) return false;
        EquipmentSlot slot = stack.getEquipmentSlot() == null ? EquipmentSlot.MAINHAND : stack.getEquipmentSlot();
        Multimap<Attribute, AttributeModifier> attributeModifiers = stack.getAttributeModifiers(slot);

        AttributeData data = getStringValueData(filter);
        AttributeCheck check = data.getValue();
        if (check == null || check.attribute == null) {
            return false;
        }
        double sumValue = attributeModifiers.get(check.attribute).stream()
                .filter(Objects::nonNull)
                .mapToDouble(AttributeModifier::getAmount)
                .sum();

        double requiredValue = check.num;
        return switch (check.mode) {
            case 1 -> sumValue >= requiredValue;
            case 2 -> sumValue <= requiredValue;
            case 3 -> sumValue > requiredValue;
            case 4 -> sumValue < requiredValue;
            case 0 -> sumValue == requiredValue;
            default -> false;
        };
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<StringValueFilterVariant> getValueVariants(ItemStack stack) {
        List<StringValueFilterVariant> variants = new ArrayList<>();
        LinkedHashSet<String> attributeIds = new LinkedHashSet<>();

        for(ResourceLocation attributeEntry : ForgeRegistries.ATTRIBUTES.getKeys()) {
            String id = attributeEntry.toString();
            if (attributeIds.add(id)) {
                StringValueFilterVariant variant = new StringValueFilterVariant(id);
                variant.title = Component.literal(id);
                variant.icon = Items.BOOK.getDefaultInstance();
                variants.add(variant);
            }
        }
        return variants;
    }

    @Override
    public String getHelpKey() {
        return "itemfilters.help_text.item_attribute";
    }
}