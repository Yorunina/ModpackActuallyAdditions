package net.yorunina.maa.model;

import net.minecraft.world.entity.item.FallingBlockEntity;

public interface IBlockPlayerInfo {
    void setBlockPlayerEntity(FallingBlockEntity entity);
    FallingBlockEntity getBlockPlayerEntity();
}
