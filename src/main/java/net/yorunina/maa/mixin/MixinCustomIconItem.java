package net.yorunina.maa.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomIconItem.class)
public abstract class MixinCustomIconItem {
    @Inject(method = "getIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z"), cancellable = true)
    private static void getIcon(CallbackInfoReturnable<Icon> cir, @Local(name = "stack")ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("IconJson")) {
            JsonElement json = JsonParser.parseString(stack.getTag().getString("IconJson"));
            cir.setReturnValue(Icon.getIcon(json));
        }
    }
}
