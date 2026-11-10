package net.yorunina.maa.client;

import io.redspace.ironsspellbooks.effect.PlanarSightEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

/** Client-only visual contribution; it never changes the player's mob effects. */
@Mod.EventBusSubscriber(modid = "maa", value = Dist.CLIENT)
public final class MAAClientEffects {
    private static final PlanarSightEffect.EcholocationBlindnessFogFunction PLANAR_FOG =
            new PlanarSightEffect.EcholocationBlindnessFogFunction();
    private static Predicate<Player> planarSightCondition = player -> false;
    private static boolean evaluated;
    private static boolean active;

    private MAAClientEffects() {
    }

    public static void setPlanarSightCondition(Predicate<Player> condition) {
        planarSightCondition = condition;
        evaluated = false;
    }

    public static boolean isPlanarSightActive() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            evaluated = false;
            active = false;
            return false;
        }
        if (!evaluated) {
            active = planarSightCondition.test(player);
            evaluated = true;
        }
        return active;
    }

    @SubscribeEvent
    public static void beginFrame(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            evaluated = false;
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var player = Minecraft.getInstance().player;
        if (player != null && !Minecraft.getInstance().isPaused() && !player.hasEffect(MobEffectRegistry.PLANAR_SIGHT.get())
                && planarSightCondition.test(player)) {
            MobEffectRegistry.PLANAR_SIGHT.get().applyEffectTick(player, 0);
        }
    }

    @SubscribeEvent
    public static void planarFog(ViewportEvent.RenderFog event) {
        var player = Minecraft.getInstance().player;
        if (player == null || event.getCamera().getEntity() != player || !isPlanarSightActive()
                || player.hasEffect(MobEffectRegistry.PLANAR_SIGHT.get())
                || player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)
                || event.getType() == FogType.LAVA || event.getType() == FogType.POWDER_SNOW) return;
        var fog = new FogRenderer.FogData(event.getMode());
        PLANAR_FOG.setupFog(fog, player, null, event.getFarPlaneDistance(), (float) event.getPartialTick());
        event.setNearPlaneDistance(fog.start);
        event.setFarPlaneDistance(fog.end);
        event.setFogShape(fog.shape);
        event.setCanceled(true);
    }
}
