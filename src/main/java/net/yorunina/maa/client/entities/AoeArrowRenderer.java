package net.yorunina.maa.client.entities;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.entities.AoeArrowEntity;
import net.yorunina.maa.entities.SeekingArrowEntity;

public class AoeArrowRenderer extends ArrowRenderer<AoeArrowEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModpackActuallyAdditions.MODID, "textures/entity/aoe_arrow.png");

    public AoeArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(AoeArrowEntity entity, BlockPos pos) {
        return 15;
    }


    public ResourceLocation getTextureLocation(AoeArrowEntity entity) {
        return TEXTURE;
    }
}