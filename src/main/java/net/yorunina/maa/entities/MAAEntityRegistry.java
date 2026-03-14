package net.yorunina.maa.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yorunina.maa.ModpackActuallyAdditions;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MAAEntityRegistry {
    public static final DeferredRegister<EntityType<?>> Defer = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModpackActuallyAdditions.MODID);
    public static final RegistryObject<EntityType<SeekingArrowEntity>> SEEKING_ARROW = Defer.register("seeking_arrow", () -> (EntityType) EntityType.Builder.of(SeekingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(SeekingArrowEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build("seeking_arrow"));
    public static final RegistryObject<EntityType<AoeArrowEntity>> AOE_ARROW = Defer.register("aoe_arrow", () -> (EntityType) EntityType.Builder.of(AoeArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(AoeArrowEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).build("aoe_arrow"));
}
