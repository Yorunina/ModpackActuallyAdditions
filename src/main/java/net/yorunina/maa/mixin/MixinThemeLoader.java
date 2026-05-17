package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbquests.quest.theme.ThemeLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThemeLoader.class)
public abstract class MixinThemeLoader {
    @ModifyExpressionValue(method = "parse(Ljava/util/Map;Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Ljava/lang/String;indexOf(I)I", ordinal = 0), remap = false)
    private static int parse(int original) {
        return original == 0 ? original : Integer.MAX_VALUE;
    }
}
