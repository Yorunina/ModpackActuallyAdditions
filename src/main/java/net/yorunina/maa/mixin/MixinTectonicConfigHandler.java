package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.yorunina.maa.compat.TectonicConfigWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedWriter;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@Pseudo
@Mixin(targets = "dev.worldgen.tectonic.config.ConfigHandler", remap = false)
public abstract class MixinTectonicConfigHandler {
    @WrapOperation(
            method = "save",
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;newBufferedWriter(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/BufferedWriter;"),
            require = 1
    )
    private static BufferedWriter maa$deferConfigWrite(Path path, OpenOption[] options, Operation<BufferedWriter> original) {
        return TectonicConfigWriter.create(path);
    }

    @WrapOperation(
            method = "load",
            at = @At(value = "INVOKE", target = "Ldev/worldgen/tectonic/config/ConfigHandler;save()V", ordinal = 1),
            require = 1
    )
    private static void maa$keepLoadedConfigAfterWritebackFailure(Operation<Void> original) {
        TectonicConfigWriter.writeBackAfterLoad(() -> original.call());
    }
}
