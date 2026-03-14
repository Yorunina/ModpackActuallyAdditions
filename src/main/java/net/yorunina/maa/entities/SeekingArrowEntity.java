package net.yorunina.maa.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class SeekingArrowEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> ARC_TOWARDS_ENTITY_ID = SynchedEntityData.defineId(SeekingArrowEntity.class, EntityDataSerializers.INT);
    private boolean stopSeeking;
    private float maxTrackDist = 10.0F;
    private float speedFactor = 1.0F;
    private float TrackExpendFactor = 0.5F;

    public SeekingArrowEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public SeekingArrowEntity(Level level, LivingEntity shooter) {
        super(MAAEntityRegistry.SEEKING_ARROW.get(), shooter, level);
    }

    public SeekingArrowEntity(Level level, double x, double y, double z) {
        super(MAAEntityRegistry.SEEKING_ARROW.get(), x, y, z, level);
    }

    public SeekingArrowEntity(AbstractArrow arrow) {
        super(MAAEntityRegistry.SEEKING_ARROW.get(), (LivingEntity) arrow.getOwner(), arrow.level());
    }

    public SeekingArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(MAAEntityRegistry.SEEKING_ARROW.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARC_TOWARDS_ENTITY_ID, -1);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    public float getMaxTrackDist() {
        return maxTrackDist;
    }

    public void setMaxTrackDist(float maxTrackDist) {
        this.maxTrackDist = maxTrackDist;
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }

    public float getTrackExpendFactor() {
        return TrackExpendFactor;
    }

    public void setTrackExpendFactor(float TrackExpendFactor) {
        this.TrackExpendFactor = TrackExpendFactor;
    }

    @Override
    public void tick() {
        super.tick();
        int id = this.getArcTowardsID();
        if (!inGround && !stopSeeking) {
            if (id == -1) {
                if (!level().isClientSide) {
                    Entity closest = null;
                    Entity owner = this.getOwner();
                    float boxExpandBy = Math.min(maxTrackDist, 3 + this.tickCount * TrackExpendFactor);
                    for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(boxExpandBy), this::canHitEntity)) {
                        if ((closest == null || entity.distanceTo(this) < closest.distanceTo(this)) && !ownedBy(entity) && (owner == null || !entity.isAlliedTo(owner))) {
                            closest = entity;
                        }
                    }
                    if (closest != null) {
                        this.setArcTowardsID(closest.getId());
                    }
                }
            } else {
                Entity arcTowards = level().getEntity(id);
                if (arcTowards != null) {
                    Vec3 arcVec = arcTowards.position().add(0, 0.65F * arcTowards.getBbHeight(), 0).subtract(this.position());
                    if(arcVec.length() > arcTowards.getBbWidth()){
                        this.setDeltaMovement(this.getDeltaMovement().scale(0.3F).add(arcVec.normalize().scale(0.7F * speedFactor)));
                    }
                }
            }
        }
        if (this.level().isClientSide && !this.inGround && id != -1) {
            Vec3 center = this.position().add(this.getDeltaMovement());
            Vec3 vec3 = center.add(new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F));
            this.level().addParticle(ParticleTypes.LAVA, center.x, center.y, center.z, vec3.x, vec3.y, vec3.z);
        }
    }

    protected void doPostHurtEffects(LivingEntity entity) {
        stopSeeking = true;
    }

    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    private int getArcTowardsID() {
        return this.entityData.get(ARC_TOWARDS_ENTITY_ID);
    }

    private void setArcTowardsID(int id) {
        this.entityData.set(ARC_TOWARDS_ENTITY_ID, id);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }
}