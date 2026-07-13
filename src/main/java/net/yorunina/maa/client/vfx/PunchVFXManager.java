package net.yorunina.maa.client.vfx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yorunina.maa.ModpackActuallyAdditions;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT})
public class PunchVFXManager {

    private static final List<ShockwaveInstance> activeShockWaves = new ArrayList<>();

    public static void addShockwave(int durationTicks, ShockwaveStyle style, Vec3 position, float yRot, float xRot) {
        activeShockWaves.add(new ShockwaveInstance(durationTicks, style, position, yRot, xRot));
    }

    public static void clearAll() {
        activeShockWaves.clear();
    }

    public static int getActiveCount() {
        return activeShockWaves.size();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Iterator<ShockwaveInstance> it = activeShockWaves.iterator();
        while (it.hasNext()) {
            ShockwaveInstance sw = it.next();
            sw.remainingTicks--;
            if (sw.remainingTicks <= 0) {
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (activeShockWaves.isEmpty()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        float tickDelta = event.getPartialTick();
        Camera camera = event.getCamera();

        for (ShockwaveInstance sw : activeShockWaves) {
            renderShockwave(sw, tickDelta, camera);
        }
    }

    private static void renderShockwave(ShockwaveInstance sw, float tickDelta, Camera camera) {
        float exactTicks = (float) sw.remainingTicks - tickDelta;
        float time = (sw.durationTicks - exactTicks) / (float) sw.durationTicks;
        time = Mth.clamp(time, 0.0F, 1.0F);

        float startTime = sw.style.startTime;
        float endTime = sw.style.endTime;

        if (time < startTime || time > endTime) return;

        float window = endTime - startTime;
        float progress = (time - startTime) / (window / sw.style.speed);
        if (progress > 1.0F) return;

        float radius = Mth.lerp(progress, sw.style.minRadius, sw.style.maxRadius);
        int alpha = (int) (255.0F * (1.0F - progress));

        PoseStack matrices = new PoseStack();
        matrices.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));

        matrices.translate(
                sw.position.x - camera.getPosition().x,
                sw.position.y - camera.getPosition().y,
                sw.position.z - camera.getPosition().z
        );

        matrices.mulPose(Axis.YP.rotationDegrees(-sw.yRot));
        matrices.mulPose(Axis.XP.rotationDegrees(sw.xRot));

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        try {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            Matrix4f matrix = matrices.last().pose();

            float fPR = Math.max(1.0F, Mth.lerp(progress, sw.style.pixelRadiusStart, sw.style.pixelRadiusEnd));
            float pixelWorldSize = radius / fPR;
            float outerR = fPR;
            float innerR = Math.max(0.0F, fPR - sw.style.ringWidth);
            int loopRadius = (int) fPR + 1;

            for (int x = 0; x <= loopRadius; ++x) {
                for (int y = 0; y <= loopRadius; ++y) {
                    float dist = (float) Math.sqrt((double) (x * x + y * y));
                    if (dist <= outerR + 0.3F && dist >= innerR - 0.3F) {
                        drawPaintPixel(matrix, buffer, x, y, pixelWorldSize, sw.style.r, sw.style.g, sw.style.b, alpha);
                        if (x != 0) {
                            drawPaintPixel(matrix, buffer, -x, y, pixelWorldSize, sw.style.r, sw.style.g, sw.style.b, alpha);
                        }
                        if (y != 0) {
                            drawPaintPixel(matrix, buffer, x, -y, pixelWorldSize, sw.style.r, sw.style.g, sw.style.b, alpha);
                        }
                        if (x != 0 && y != 0) {
                            drawPaintPixel(matrix, buffer, -x, -y, pixelWorldSize, sw.style.r, sw.style.g, sw.style.b, alpha);
                        }
                    }
                }
            }

            tesselator.end();
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private static void drawPaintPixel(Matrix4f matrix, BufferBuilder buffer, int gridX, int gridY, float pixelScale, int r, int g, int b, int alpha) {
        float cx = (float) gridX * pixelScale;
        float cy = (float) gridY * pixelScale;
        float half = pixelScale * 0.5F;
        buffer.vertex(matrix, cx - half, cy - half, 0.0F).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, cx + half, cy - half, 0.0F).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, cx + half, cy + half, 0.0F).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, cx - half, cy + half, 0.0F).color(r, g, b, alpha).endVertex();
    }

    public static class ShockwaveStyle {
        public static final ShockwaveStyle DEFAULT = builder().build();

        public final int r, g, b;
        public final float minRadius;
        public final float maxRadius;
        public final float ringWidth;
        public final float speed;
        public final float startTime;
        public final float endTime;
        public final float pixelRadiusStart;
        public final float pixelRadiusEnd;
        public final float forwardOffset;

        public ShockwaveStyle(int r, int g, int b, float minRadius, float maxRadius, float ringWidth, float speed, float startTime, float endTime, float pixelRadiusStart, float pixelRadiusEnd, float forwardOffset) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
            this.ringWidth = ringWidth;
            this.speed = speed;
            this.startTime = startTime;
            this.endTime = endTime;
            this.pixelRadiusStart = pixelRadiusStart;
            this.pixelRadiusEnd = pixelRadiusEnd;
            this.forwardOffset = forwardOffset;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int r = 240, g = 250, b = 255;
            private float minRadius = 0.5F;
            private float maxRadius = 4.0F;
            private float ringWidth = 2.0F;
            private float speed = 1.5F;
            private float startTime = 0.25F;
            private float endTime = 0.65F;
            private float pixelRadiusStart = 4.0F;
            private float pixelRadiusEnd = 32.0F;
            private float forwardOffset = 2.5F;

            public Builder color(int r, int g, int b) {
                this.r = r;
                this.g = g;
                this.b = b;
                return this;
            }

            public Builder minRadius(float minRadius) {
                this.minRadius = minRadius;
                return this;
            }

            public Builder maxRadius(float maxRadius) {
                this.maxRadius = maxRadius;
                return this;
            }

            public Builder ringWidth(float ringWidth) {
                this.ringWidth = ringWidth;
                return this;
            }

            public Builder speed(float speed) {
                this.speed = speed;
                return this;
            }

            public Builder startTime(float startTime) {
                this.startTime = startTime;
                return this;
            }

            public Builder endTime(float endTime) {
                this.endTime = endTime;
                return this;
            }

            public Builder pixelRadiusStart(float pixelRadiusStart) {
                this.pixelRadiusStart = pixelRadiusStart;
                return this;
            }

            public Builder pixelRadiusEnd(float pixelRadiusEnd) {
                this.pixelRadiusEnd = pixelRadiusEnd;
                return this;
            }

            public Builder forwardOffset(float forwardOffset) {
                this.forwardOffset = forwardOffset;
                return this;
            }

            public ShockwaveStyle build() {
                return new ShockwaveStyle(r, g, b, minRadius, maxRadius, ringWidth, speed, startTime, endTime, pixelRadiusStart, pixelRadiusEnd, forwardOffset);
            }
        }
    }

    private static class ShockwaveInstance {
        int remainingTicks;
        final int durationTicks;
        final ShockwaveStyle style;
        final Vec3 position;
        final float yRot;
        final float xRot;

        ShockwaveInstance(int durationTicks, ShockwaveStyle style, Vec3 position, float yRot, float xRot) {
            this.durationTicks = durationTicks;
            this.remainingTicks = durationTicks;
            this.style = style;
            this.position = position;
            this.yRot = yRot;
            this.xRot = xRot;
        }
    }
}