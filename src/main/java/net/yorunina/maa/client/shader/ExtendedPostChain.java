package net.yorunina.maa.client.shader;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.mixin.accessor.PostChainAccessor;

import java.io.IOException;
import java.util.List;

public class ExtendedPostChain extends PostChain {
    public ExtendedPostChain(String name) throws JsonSyntaxException, IOException {
        super(
                Minecraft.getInstance().getTextureManager(),
                Minecraft.getInstance().getResourceManager(),
                Minecraft.getInstance().getMainRenderTarget(),
                ModpackActuallyAdditions.id("shaders/post/" + name + ".json")
        );
        Window window = Minecraft.getInstance().getWindow();
        this.resize(window.getWidth(), window.getHeight());
    }

    public EffectInstance getMainShader() {
        List<PostPass> passes = ((PostChainAccessor) this).maa$getPasses();
        return passes.get(0).getEffect();
    }

    @Override
    public void process(float partialTicks) {
        Window window = Minecraft.getInstance().getWindow();
        PostChainAccessor accessor = (PostChainAccessor) this;
        if (accessor.maa$getScreenWidth() != window.getWidth() || accessor.maa$getScreenHeight() != window.getHeight()) {
            this.resize(window.getWidth(), window.getHeight());
        }
        super.process(partialTicks);
    }
}
