package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Pseudo
@Mixin(targets = "com.github.alexthe666.citadel.Citadel", remap = false)
public abstract class MixinCitadel {
    @WrapOperation(
            method = "lambda$setup$10()V",
            at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/citadel/web/WebHelper;getURLContents(Ljava/lang/String;Ljava/lang/String;)Ljava/io/BufferedReader;"),
            require = 1
    )
    private static BufferedReader maa$readBundledContributors(String url, String resourcePath, Operation<BufferedReader> original) {
        // Keep the surrounding setup and packet registrations; replace only the remote list read.
        Path resource = ModList.get().getModFileById("citadel").getFile().findResource(resourcePath);
        try {
            // Citadel does not close this reader, so close the actual file before handing it over.
            return new BufferedReader(new StringReader(Files.readString(resource, StandardCharsets.UTF_8)));
        } catch (IOException exception) {
            // Preserve Citadel's existing missing-list warning without falling back to the network.
            return null;
        }
    }
}
