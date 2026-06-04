package net.yorunina.maa.mixin;

import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Mixin(SkinLibrary.class)
public abstract class MixinSkinLibrary {
    @ModifyVariable(method = "reloadFiles", at = @At("HEAD"), argsOnly = true, remap = false)
    public ArrayList<SkinLibraryFile> reloadSkinLibrary(ArrayList<SkinLibraryFile> arg0) {
        return arg0.stream().filter(file -> !file.path().startsWith("/secret")).collect(Collectors.toCollection(ArrayList::new));
    }
}
