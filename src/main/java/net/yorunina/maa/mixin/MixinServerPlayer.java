package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.yorunina.maa.model.IPlayer;
import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {
    @Shadow
    public abstract void stopRiding();

    public MixinServerPlayer(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnectInject(CallbackInfo ci) {
        if (this.isPassenger()) {
            if (this.getVehicle() instanceof Player)
                this.stopRiding();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickInjectRemovePassengerOnInterruption(CallbackInfo callbackInfo) {
        if (this.isVehicle() && !this.isCrouching() && !this.isFallFlying() && this.fallDistance > 2F)
            if (this.getFirstPassenger() != null)
                this.getFirstPassenger().stopRiding();
    }

    @ModifyVariable(method = "startRiding", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Entity startRidingModifyParameter(Entity entity) {
        Entity lastPassenger = entity;
        while (lastPassenger instanceof Player player && player.getFirstPassenger() instanceof Player playerEntity) {
            lastPassenger = playerEntity;
        }

        if (lastPassenger != null) {
            return lastPassenger;
        }
        return entity;
    }

    // Send sync packet to prevent client desync from riding multiple players.
    @Inject(method = "startRiding", at = @At("RETURN"))
    private void startRidingInjectSyncPacket(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ServerPlayer playerEntity && cir.getReturnValue()) {
            playerEntity.connection.send(new ClientboundSetPassengersPacket(playerEntity));
        }
    }

    @ModifyExpressionValue(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean restoreFromModifyExpressionValue(boolean bool, @Local(name = "p_9016_") ServerPlayer serverPlayer) {
        return bool || ((IPlayer) serverPlayer).shouldKeepInventory();
    }
}