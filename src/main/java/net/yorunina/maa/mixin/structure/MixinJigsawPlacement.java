package net.yorunina.maa.mixin.structure;

import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value={JigsawPlacement.class}, priority=999)
public class MixinJigsawPlacement {
    @ModifyConstant(method={"generateJigsaw"}, constant={@Constant(intValue=128)}, require=0)
    private static int changeMaxGenDistance(int value) {
        return 512;
    }
}