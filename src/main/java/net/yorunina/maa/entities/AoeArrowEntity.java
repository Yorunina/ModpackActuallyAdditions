package net.yorunina.maa.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class AoeArrowEntity extends AbstractArrow {
    private float aoeSize = 1.0F;
    private float damageFactor = 0.1F;
    private boolean allowEntityAoe = false;
    private boolean doneAoe = false;

    public AoeArrowEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public AoeArrowEntity(Level level, LivingEntity shooter) {
        super(MAAEntityRegistry.AOE_ARROW.get(), shooter, level);
    }

    public AoeArrowEntity(Level level, double x, double y, double z) {
        super(MAAEntityRegistry.AOE_ARROW.get(), x, y, z, level);
    }

    public AoeArrowEntity(AbstractArrow arrow) {
        super(MAAEntityRegistry.AOE_ARROW.get(), (LivingEntity) arrow.getOwner(), arrow.level());
    }

    public AoeArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(MAAEntityRegistry.AOE_ARROW.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    public void setAoeSize(float aoeSize) {
        this.aoeSize = aoeSize;
    }

    public void setDamageFactor(float damageFactor) {
        this.damageFactor = damageFactor;
    }

    public void setAllowEntityAoe(boolean allowEntityAoe) {
        this.allowEntityAoe = allowEntityAoe;
    }

    public float getAoeSize() {
        return this.aoeSize;
    }

    public float getDamageFactor() {
        return this.damageFactor;
    }

    public boolean isAllowEntityAoe() {
        return this.allowEntityAoe;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.inGround) {
            Vec3 center = this.position().add(this.getDeltaMovement());
            Vec3 vec3 = center.add(new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F));
            this.level().addParticle(ParticleTypes.LAVA, center.x, center.y, center.z, vec3.x, vec3.y, vec3.z);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.doneAoe) return;
        if (!this.allowEntityAoe) return;

        if (this.getOwner() == null || !(this.getOwner() instanceof LivingEntity owner)) return;
        Level level = this.level();
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
        AABB aabb = new AABB(this.getX() - this.aoeSize, this.getY() - this.aoeSize, this.getZ() - this.aoeSize,
                this.getX() + this.aoeSize, this.getY() + this.aoeSize, this.getZ() + this.aoeSize);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
            float damage = (float) (owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.damageFactor);
            entity.hurt(level.damageSources().mobAttack(owner), damage);
        }
        this.doneAoe = true;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.doneAoe) return;
        if (this.getOwner() == null || !(this.getOwner() instanceof LivingEntity owner)) return;
        Level level = this.level();
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
        AABB aabb = new AABB(this.getX() - this.aoeSize, this.getY() - this.aoeSize, this.getZ() - this.aoeSize,
                this.getX() + this.aoeSize, this.getY() + this.aoeSize, this.getZ() + this.aoeSize);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
            float damage = (float) (owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.damageFactor);
            entity.hurt(level.damageSources().mobAttack(owner), damage);
        }
        this.doneAoe = true;
    }


    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }


    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }
}