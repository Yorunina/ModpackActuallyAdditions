package net.yorunina.maa.client.entities;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.yorunina.maa.entities.MAAEntityRegistry;

public class ClientRenderRegistry {
    public static void init() {
        EntityRenderers.register(MAAEntityRegistry.SEEKING_ARROW.get(), SeekingArrowRenderer::new);
        EntityRenderers.register(MAAEntityRegistry.AOE_ARROW.get(), AoeArrowRenderer::new);
    }
}
