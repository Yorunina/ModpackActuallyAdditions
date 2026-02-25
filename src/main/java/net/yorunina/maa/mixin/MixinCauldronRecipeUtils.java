package net.yorunina.maa.mixin;

import net.mehvahdjukaar.amendments.common.recipe.CauldronRecipeUtils;
import net.mehvahdjukaar.amendments.common.recipe.FluidAndItemCraftResult;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yorunina.maa.compat.kubejs.events.CauldronCraftEventJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static net.yorunina.maa.compat.kubejs.MAAEvents.CAULDRON_CRAFT_EVENT;

@Mixin(value = CauldronRecipeUtils.class, remap = false)
public class MixinCauldronRecipeUtils {
    @Inject(method = "craft", at = @At(
            value = "INVOKE",
            target = "Lnet/mehvahdjukaar/amendments/common/recipe/CauldronRecipeUtils;craftFluidSpecial(Lnet/minecraft/world/level/Level;ZILnet/mehvahdjukaar/moonlight/api/fluids/SoftFluidStack;Ljava/util/Collection;)Lnet/mehvahdjukaar/amendments/common/recipe/FluidAndItemCraftResult;"),
            cancellable = true)
    private static void craftMixin(Level level, boolean boiling, int tankCapacity, SoftFluidStack fluidStack, List<ItemStack> items, CallbackInfoReturnable<FluidAndItemCraftResult> cir) {
        // 将不可访问的ImmutableCollections$List12转换为可访问的ArrayList
        List<ItemStack> accessibleItems = new ArrayList<>(items);
        CauldronCraftEventJS event = new CauldronCraftEventJS(level, boiling, tankCapacity, fluidStack, accessibleItems);
        CAULDRON_CRAFT_EVENT.post(event);
        if (event.getCraftResult() != null) {
            cir.setReturnValue(event.getCraftResult());
        }
    }
}
