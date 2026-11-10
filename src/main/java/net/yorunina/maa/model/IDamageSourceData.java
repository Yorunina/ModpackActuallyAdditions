package net.yorunina.maa.model;

import net.minecraft.nbt.CompoundTag;

/** Transient data belonging to one damage source, without world persistence or network synchronization. */
public interface IDamageSourceData {
    CompoundTag getCustomData();

    boolean hasCustomData();
}
