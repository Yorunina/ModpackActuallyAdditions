package net.yorunina.maa.mixin.accessor;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PostChain.class)
public interface PostChainAccessor {
    @Accessor("passes")
    List<PostPass> maa$getPasses();

    @Accessor("screenWidth")
    int maa$getScreenWidth();

    @Accessor("screenHeight")
    int maa$getScreenHeight();
}