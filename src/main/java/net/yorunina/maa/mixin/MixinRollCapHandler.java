package net.yorunina.maa.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.unusual.block_factorys_bosses.capability.entity.RollCap;
import net.yorunina.maa.compat.kubejs.events.RollEventJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.yorunina.maa.compat.kubejs.MAAEvents.PLAYER_ROLL;

@Mixin(value = RollCap.RollCapHandler.class, remap = false)
public abstract class MixinRollCapHandler {

    @Shadow
    protected int roll;

    @Inject(method = "startRoll", at = @At("HEAD"), cancellable = true)
    private void onRoll(Player player, float leftImpulse, float forwardImpulse, CallbackInfoReturnable<Boolean> cir) {
        if (player.level().isClientSide()) {
            return;
        }
        if (this.roll > -5) {
            return;
        }
        if (!player.onGround()) {
            return;
        }

        RollCap.RollCapHandler.RollType direction = computeDirection(player, leftImpulse, forwardImpulse);

        PLAYER_ROLL.post(new RollEventJS(player, leftImpulse, forwardImpulse, direction));
    }


    @Unique
    private static RollCap.RollCapHandler.RollType computeDirection(Player player, float leftImpulse, float forwardImpulse) {
        float speed = player.getSpeed();
        float a = speed * leftImpulse;
        float b = speed * forwardImpulse;
        float yawRad = player.getYRot() * 0.017453292f;
        float sin = Mth.sin(yawRad);
        float cos = Mth.cos(yawRad);
        double vx = (a * cos) - (b * sin);
        double vy = (b * cos) + (a * sin);

        if (vx * vx + vy * vy > 0.001) {
            if (Math.abs(forwardImpulse) < Math.abs(leftImpulse)) {
                return leftImpulse > 0 ? RollCap.RollCapHandler.RollType.LEFT : RollCap.RollCapHandler.RollType.RIGHT;
            }
            return forwardImpulse > 0 ? RollCap.RollCapHandler.RollType.FORWARD : RollCap.RollCapHandler.RollType.BACKWARD;
        }
        return RollCap.RollCapHandler.RollType.FORWARD;
    }
}
