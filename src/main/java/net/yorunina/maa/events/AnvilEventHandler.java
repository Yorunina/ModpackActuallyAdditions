package net.yorunina.maa.events;

import io.github.lightman314.lightmanscurrency.common.core.ModEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yorunina.maa.ModpackActuallyAdditions;

import java.util.Map;

import static net.yorunina.maa.ModpackActuallyAdditions.MODID;


@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilEventHandler {
    static TagKey<Item> CANNOT_MENDING = TagKey.create(Registries.ITEM, ModpackActuallyAdditions.id("cannot_mending"));

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        ItemStack out = event.getOutput();

        if (out.isEmpty() && (left.isEmpty() || right.isEmpty())) {
            return;
        }

        Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);
        if (right.getItem() == Items.ENCHANTED_BOOK && (enchRight.containsKey(Enchantments.MENDING) || enchRight.containsKey(ModEnchantments.MONEY_MENDING.get())) && left.is(CANNOT_MENDING)) {
            if (out.isEmpty()) {
                out = left.copy();
            }
            Map<Enchantment, Integer> enchOutput = EnchantmentHelper.getEnchantments(out);
            enchOutput.putAll(enchRight);
            enchOutput.remove(Enchantments.MENDING);
            EnchantmentHelper.setEnchantments(enchOutput, out);
            event.setOutput(out);
        }
    }
}
