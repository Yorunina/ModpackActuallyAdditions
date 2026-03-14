package net.yorunina.maa.client.entities;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.entities.SeekingArrowEntity;

public class SeekingArrowRenderer extends ArrowRenderer<SeekingArrowEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModpackActuallyAdditions.MODID, "textures/entity/seeking_arrow.png");

    public SeekingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(SeekingArrowEntity entity, BlockPos pos) {
        return 15;
    }


    public ResourceLocation getTextureLocation(SeekingArrowEntity entity) {
        return TEXTURE;
    }
}