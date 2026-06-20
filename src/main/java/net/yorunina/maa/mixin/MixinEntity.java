package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.yorunina.maa.MAATags;
import net.yorunina.maa.model.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {
    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract void clearFire();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Entity getVehicle();

    @Shadow
    public abstract void stopRiding();

    @Shadow
    public abstract boolean is(Entity p_20356_);

    @Unique
    public boolean noFireDamage = false;

    @Unique
    public boolean keepOutRain = false;

    @Unique
    public void setNoFireDamage(boolean value) {
        this.noFireDamage = value;
    }

    @Unique
    public boolean getNoFireDamage() {
        return this.noFireDamage;
    }

    @Unique
    public boolean getKeepOutRain() {
        return this.keepOutRain;
    }

    @Unique
    public void setKeepOutRain(boolean value) {this.keepOutRain = value;}

    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean baseTickHut(Entity instance, DamageSource p_19946_, float p_19947_, Operation<Boolean> original) {
        if (noFireDamage) return true;
        return original.call(instance, p_19946_, p_19947_);
    }


    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            ), method = "thunderHit", cancellable = true
    )
    private void thunderHitHurt(ServerLevel p_19927_, LightningBolt p_19928_, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().is(MAATags.IMMUNE_LIGHTNING)) {
                ci.cancel();
            }
            if (itemEntity.getItem().is(MAATags.IMMUNE_FIRE)) {
                clearFire();
            }
        }
    }


    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void removePassengerInject(Entity passenger, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide) return;
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetPassengersPacket(entity));
        }

        if (passenger instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetPassengersPacket(passenger));
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addPassenger(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER))
    private void startRidingInject(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (entity.level().isClientSide) return;
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetPassengersPacket(entity));
        }
        Entity passenger = (Entity) (Object) this;
        if (passenger instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetPassengersPacket(passenger));
        }
    }

    @Inject(method = "isInRain", at = @At("HEAD"), cancellable = true)
    private void oriacs$checkUmbrella(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity && this.keepOutRain) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void restoreFromInject(Entity p_20356_, CallbackInfo ci) {
        this.noFireDamage = ((IEntity) p_20356_).getNoFireDamage();
        this.keepOutRain = ((IEntity) p_20356_).getKeepOutRain();
    }
}
