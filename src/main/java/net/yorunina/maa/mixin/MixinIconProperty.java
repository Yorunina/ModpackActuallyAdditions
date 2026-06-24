package net.yorunina.maa.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.theme.property.IconProperty;
import net.yorunina.maa.ModpackActuallyAdditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IconProperty.class)
public abstract class MixinIconProperty {
    @Inject(method = "parse(Ljava/lang/String;)Ldev/ftb/mods/ftblibrary/icon/Icon;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void parse(String string, CallbackInfoReturnable<Icon> cir) {
        if (string.startsWith("{")) {
            JsonElement jsonElement = JsonParser.parseString(string);
            if  (jsonElement.isJsonNull()) {
                return;
            }
            cir.setReturnValue(Icon.getIcon(jsonElement));
        }
    }
}
