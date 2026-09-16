package net.yorunina.maa.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.compat.kubejs.MAAUtils;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MAAShaders implements ResourceManagerReloadListener {
    private static final float OUTER_RADIUS = 10.0F;
    private static final float MAX_DISTANCE = 128.0F;
    private static final float MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;
    private static final Matrix4f INVERSE_MAT = new Matrix4f();
    private static ExtendedPostChain WORMHOLE;

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        if (WORMHOLE != null) {
            WORMHOLE.close();
            WORMHOLE = null;
        }
        try {
            WORMHOLE = new ExtendedPostChain("wormhole");
        } catch (Exception e) {
            ModpackActuallyAdditions.LOGGER.error("Failed to reload MAA post shaders", e);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || WORMHOLE == null || event.getFrustum() == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !MAAUtils.hasShaderEntities()) {
            return;
        }

        Camera camera = event.getCamera();
        Frustum frustum = event.getFrustum();
        float frameTime = event.getPartialTick();
        Vec3 camPos = camera.getPosition();
        double closestDistSq = Double.POSITIVE_INFINITY;
        double closestX = 0.0;
        double closestY = 0.0;
        double closestZ = 0.0;

        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!MAAUtils.isShaderEntity(entity)) {
                continue;
            }
            double x = Mth.lerp(frameTime, entity.xo, entity.getX());
            double y = Mth.lerp(frameTime, entity.yo, entity.getY()) + entity.getBbHeight() * 0.5F;
            double z = Mth.lerp(frameTime, entity.zo, entity.getZ());
            double distSq = camPos.distanceToSqr(x, y, z);
            if (distSq > MAX_DISTANCE_SQ || distSq >= closestDistSq) {
                continue;
            }
            if (!frustum.isVisible(new AABB(
                    x - OUTER_RADIUS, y - OUTER_RADIUS, z - OUTER_RADIUS,
                    x + OUTER_RADIUS, y + OUTER_RADIUS, z + OUTER_RADIUS
            ))) {
                continue;
            }
            closestDistSq = distSq;
            closestX = x;
            closestY = y;
            closestZ = z;
        }

        if (closestDistSq == Double.POSITIVE_INFINITY) {
            return;
        }

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        poseStack.translate(closestX - camPos.x, closestY - camPos.y, closestZ - camPos.z);

        Matrix4f projection = event.getProjectionMatrix();
        EffectInstance shader = WORMHOLE.getMainShader();
        shader.safeGetUniform("InverseTransformMatrix").set(INVERSE_MAT.identity().mul(projection).mul(poseStack.last().pose()).invert());
        shader.safeGetUniform("ProjectionMatrix").set(projection);
        shader.safeGetUniform("ViewMatrix").set(poseStack.last().pose());

        RenderSystem.depthMask(false);
        WORMHOLE.process(frameTime);
        minecraft.getMainRenderTarget().bindWrite(false);
        RenderSystem.depthMask(true);
    }
}
