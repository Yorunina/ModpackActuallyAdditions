package net.yorunina.maa.model;

import org.spongepowered.asm.mixin.Unique;

public interface IEntity {
    void setNoFireDamage(boolean value);
    boolean getNoFireDamage();
    void setKeepOutRain(boolean value);
    boolean getKeepOutRain();

    /** 描边颜色（RGB）；0 表示不发光。 */
    int getGlowRgb();
    void setGlowRgb(int rgb);

    /** 描边到期时刻（世界游戏时间秒）；0 表示永久。 */
    int getGlowEnd();
    void setGlowEnd(int endSecond);
}
