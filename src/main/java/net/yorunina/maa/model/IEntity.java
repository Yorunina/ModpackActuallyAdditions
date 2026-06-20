package net.yorunina.maa.model;

import org.spongepowered.asm.mixin.Unique;

public interface IEntity {
    void setNoFireDamage(boolean value);
    boolean getNoFireDamage();
    void setKeepOutRain(boolean value);
    boolean getKeepOutRain();
}
