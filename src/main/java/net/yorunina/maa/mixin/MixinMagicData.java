package net.yorunina.maa.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.rain.arcane_convergence.MagicDataPlayer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MagicData.class)
public abstract class MixinMagicData implements MagicDataPlayer {
    @Shadow
    private ServerPlayer serverPlayer;

    @Shadow
    public abstract void setMana(float mana);

    @Shadow
    public abstract float getMana();

    public ServerPlayer getPlayer() {
        return this.serverPlayer;
    }

    @Inject(method = "addMana", at = @At("HEAD"), cancellable = true, remap = false)
    public void addMana(float mana, CallbackInfo ci) {
        this.setMana(this.getMana() + mana);
        ci.cancel();
    }
}
