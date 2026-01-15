package net.yorunina.ftbqaa.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.gui.CustomToast;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.yorunina.ftbqaa.client.RenderLiquid;
import net.yorunina.ftbqaa.network.SubmitStructurePacket;
import net.yorunina.ftbqaa.tasks.StructureTask;
import net.yorunina.ftbqaa.utils.StructureLoader;


@EventBusSubscriber({Dist.CLIENT})
public class StructurePlacementClient {
    private static boolean locked = false;
    private static BlockPos base;
    private static Rotation rot;
    public static List<StructureTemplate.StructureBlockInfo> template;
    private static boolean show;
    private static boolean showLayers;
    static StructureTask currentTask;
    private static Minecraft mc;
    private static fakeworld fake;
    private static List<BlockCache> cache;

    @SubscribeEvent
    public static void render(PlayerInteractEvent.RightClickBlock event) {
        if (show && event.getSide() == LogicalSide.CLIENT) {
            if (currentTask != null && currentTask.rightclick_validation) {
                if (locked) {
                    if (ItemFiltersAPI.filter(currentTask.item, event.getItemStack())) {
                        submit(currentTask.id);
                    }
                } else if (event.getHand() == InteractionHand.OFF_HAND || !event.getItemStack().isEmpty()) {
                    locked = true;
                }
            } else {
                locked = true;
            }
        }

    }

    private static void submit(long id) {
        boolean ignoreState = currentTask.ignoreState;
        currentTask = null;
        show = false;
        ClientLevel world = mc.level;
        getFake().clearState();

        for(StructureTemplate.StructureBlockInfo info : template) {
            BlockPos pos = base.offset(info.pos().above(1).rotate(rot));
            BlockState state = world.getBlockState(pos);
            BlockState state2 = info.state().rotate(rot);
            if (!StructureLoader.isValidState(state, state2, ignoreState)) {
                mc.getToasts().addToast(new CustomToast(Component.literal("Structure incomplete!"), ItemIcon.getItemIcon((Item)FTBQuestsItems.MISSING_ITEM.get()), Component.literal("Structure incomplete!")));
                mc.player.displayClientMessage(Component.literal("Invalid block at pos " + pos), false);
                mc.player.displayClientMessage(Component.literal("Should be " + state2 + " but is " + state), false);
                locked = false;
                return;
            }
        }

        (new SubmitStructurePacket(id, base, rot)).sendToServer();
        locked = false;
    }

    public static boolean isLocked() {
        return locked;
    }

    public static void toggleShowStructure(String name, boolean layer, StructureTask task) {
        show = true;
        showLayers = layer;
        template = StructureLoader.get(name);
        if (template == null) {
            show = false;
            mc.getToasts().addToast(new CustomToast(Component.literal("No valid structure!"), ItemIcon.getItemIcon((Item)FTBQuestsItems.MISSING_ITEM.get()), Component.literal("Report this bug to modpack author!")));
        } else {
            currentTask = task;
            if (locked) {
                submit(task.id);
                return;
            }

            getFake().setBlockStates(template);
            cache.clear();
        }

        locked = false;
    }

    @SubscribeEvent
    public static void render(TickEvent.ClientTickEvent event) {
        if (show && event.phase == Phase.END && !locked && mc.hitResult != null && Minecraft.getInstance().player != null && Minecraft.getInstance().hitResult.getType() == Type.BLOCK) {
            Rotation prevRot = rot;
            BlockPos prevBase = base;
            base = ((BlockHitResult)mc.hitResult).getBlockPos().below();
            rot = StructureLoader.getRotation(Direction.fromYRot((double)mc.player.getYRot()));
            if (prevRot != rot) {
                getFake().setBlockStates(template);
                cache.clear();
            } else if (!prevBase.equals(base)) {
                cache.clear();
            }
        }

    }

    private static fakeworld getFake() {
        if (fake == null) {
            fake = new fakeworld(mc.level);
        }

        return fake;
    }

    private static List<BlockCache> getCache() {
        if (cache.isEmpty()) {
            for(StructureTemplate.StructureBlockInfo info : template) {
                cache.add(new BlockCache(info, base, rot));
            }
        }

        return cache;
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() == Stage.AFTER_BLOCK_ENTITIES && show && getFake().hasStates()) {
            PoseStack matrix = event.getPoseStack();
            double renderPosX = -mc.gameRenderer.getMainCamera().getPosition().x();
            double renderPosY = -mc.gameRenderer.getMainCamera().getPosition().y();
            double renderPosZ = -mc.gameRenderer.getMainCamera().getPosition().z();
            BlockRenderDispatcher brd = mc.getBlockRenderer();
            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
            ClientLevel world = mc.level;
            int layer = -1;
            matrix.pushPose();
            matrix.translate(renderPosX, renderPosY, renderPosZ);

            for(BlockCache cache : getCache()) {
                if ((layer == -1 || cache.info.pos().getY() == layer || !locked) && !world.getBlockState(cache.pos).equals(cache.state)) {
                    if (showLayers && (cache.state.getFluidState().isSource() || cache.state.getFluidState().isEmpty())) {
                        layer = cache.info.pos().getY();
                    }

                    cache.render(matrix, brd, world, buffer);
                }
            }

            buffer.endBatch();
            matrix.popPose();
        }

    }

    static {
        base = BlockPos.ZERO;
        rot = Rotation.CLOCKWISE_180;
        template = null;
        show = false;
        showLayers = false;
        mc = Minecraft.getInstance();
        cache = new ArrayList();
    }

    public static class render extends RenderType {
        public static final RenderType preview;
        public static final RenderType LiquidPreview;

        private render(String name, boolean alpha, VertexFormat format, ImmutableList<RenderStateShard> phases) {
            super(name, format, Mode.QUADS, 256, true, true, alpha ? () -> {
                phases.forEach(RenderStateShard::setupRenderState);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.69F);
            } : () -> phases.forEach(RenderStateShard::setupRenderState), () -> phases.forEach(RenderStateShard::clearRenderState));
        }
        static {
            preview = new render("preview_white", true, DefaultVertexFormat.BLOCK, ImmutableList.of(
                    RenderStateShard.BLOCK_SHEET_MIPPED,
                    RenderStateShard.TRANSLUCENT_TRANSPARENCY,
                    RenderStateShard.LIGHTMAP,
                    RenderStateShard.CULL,
                    RenderStateShard.LEQUAL_DEPTH_TEST,
                    RenderStateShard.NO_OVERLAY,
                    RenderStateShard.NO_COLOR_LOGIC,
                    RenderStateShard.NO_LAYERING,
                    RenderStateShard.MAIN_TARGET,
                    RenderStateShard.COLOR_DEPTH_WRITE,
                    RenderStateShard.DEFAULT_LINE,
                    RenderStateShard.RENDERTYPE_SOLID_SHADER,
                    new RenderStateShard[0]));
            LiquidPreview = new render("LiquidPreview", false, DefaultVertexFormat.NEW_ENTITY, ImmutableList.of(
                    RenderStateShard.BLOCK_SHEET_MIPPED,
                    RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER,
                    RenderStateShard.LIGHTMAP,
                    RenderStateShard.CULL,
                    RenderStateShard.LEQUAL_DEPTH_TEST,
                    RenderStateShard.NO_OVERLAY,
                    RenderStateShard.NO_COLOR_LOGIC,
                    RenderStateShard.NO_LAYERING,
                    RenderStateShard.MAIN_TARGET,
                    RenderStateShard.COLOR_DEPTH_WRITE,
                    RenderStateShard.DEFAULT_LINE,
                    RenderStateShard.RENDERTYPE_SOLID_SHADER,
                    new RenderStateShard[0]));
        }
    }

    private static class BlockCache {
        final BlockPos pos;
        final BlockState state;
        final BlockEntity tileentity;
        final ModelData data;
        final float[] color;
        final StructureTemplate.StructureBlockInfo info;

        public BlockCache(StructureTemplate.StructureBlockInfo info, BlockPos base, Rotation rot) {
            this.info = info;
            this.pos = base.offset(info.pos().above(1).rotate(rot));
            this.state = info.state().rotate(rot);
            if (this.state.hasBlockEntity()) {
                this.tileentity = BlockEntity.loadStatic(info.pos().rotate(rot), this.state, info.nbt());
                this.tileentity.setLevel(StructurePlacementClient.getFake());
                this.data = this.tileentity.getModelData();
            } else {
                this.data = ModelData.EMPTY;
                this.tileentity = null;
            }

            int color = StructurePlacementClient.mc.getBlockColors().getColor(this.state, null, null, 0);
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;
            this.color = new float[]{r, g, b};
        }

        public void render(PoseStack matrix, BlockRenderDispatcher brd, Level realWorld, MultiBufferSource.BufferSource buffer) {
            matrix.pushPose();
            matrix.translate((float)this.pos.getX(), (float)this.pos.getY(), (float)this.pos.getZ());
            if (this.tileentity != null) {
                StructurePlacementClient.getFake().setDefault(this.state);
                StructurePlacementClient.mc.getBlockEntityRenderDispatcher().renderItem(this.tileentity, matrix, buffer, 15991024, OverlayTexture.pack(OverlayTexture.u(0.3F), realWorld.getBlockState(this.pos).isAir() ? 10 : 3));
                StructurePlacementClient.getFake().resetDefault();
            }

            BlockState state2 = this.state;
            if (state2.isAir()) {
                state2 = realWorld.getBlockState(this.pos);
            }

            BakedModel model = brd.getBlockModel(state2);
            if (!state2.getFluidState().isSource()) {
                RenderLiquid.tesselate(matrix.last().pose(), StructurePlacementClient.getFake(), this.info.pos().rotate(StructurePlacementClient.rot), buffer.getBuffer(StructurePlacementClient.render.LiquidPreview), state2.getFluidState());
            }

            if (state2.getRenderShape() != RenderShape.INVISIBLE) {
                brd.getModelRenderer().renderModel(matrix.last(), buffer.getBuffer(StructurePlacementClient.render.preview), state2, model, this.color[0], this.color[1], this.color[2], 15991024, OverlayTexture.pack(OverlayTexture.u(0.3F), realWorld.getBlockState(this.pos).isAir() ? 10 : 3), this.data, StructurePlacementClient.render.preview);
            }

            matrix.popPose();
        }
    }

    private static class fakeworld extends Level {
        private Map<BlockPos, BlockState> states = Maps.newHashMap();
        private Level world;
        private BlockState defaultstate;
        private final TransientEntitySectionManager<Entity> entityStorage;

        protected fakeworld(Level world) {
            super((WritableLevelData)world.getLevelData(), world.dimension(), world.registryAccess(), world.dimensionTypeRegistration(), world.getProfilerSupplier(), true, true, 0L, 0);
            this.defaultstate = Blocks.AIR.defaultBlockState();
            this.entityStorage = new TransientEntitySectionManager<>(Entity.class, new EntityCallbacks());
            this.world = world;
        }

        public <T> LazyOptional<T> getCapability(Capability<T> cap) {
            return this.world.getCapability(cap);
        }

        public void clearState() {
            this.states.clear();
        }

        public boolean hasStates() {
            return !this.states.isEmpty();
        }

        public void setBlockStates(List<StructureTemplate.StructureBlockInfo> template) {
            this.states.clear();

            for(StructureTemplate.StructureBlockInfo info : template) {
                this.states.put(info.pos().rotate(StructurePlacementClient.rot), info.state().rotate(StructurePlacementClient.rot));
            }

        }

        public void setDefault(BlockState state) {
            this.defaultstate = state;
        }

        public void resetDefault() {
            this.defaultstate = Blocks.AIR.defaultBlockState();
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return this.world.getBlockEntity(pos);
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return this.states.getOrDefault(pos, this.defaultstate);
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.getBlockState(pos).getFluidState();
        }

        @Override
        public LevelTickAccess<Block> getBlockTicks() {
            return this.world.getBlockTicks();
        }

        @Override
        public LevelTickAccess<Fluid> getFluidTicks() {
            return this.world.getFluidTicks();
        }

        @Override
        public ChunkSource getChunkSource() {
            return this.world.getChunkSource();
        }

        @Override
        public void levelEvent(Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
        }

        @Override
        public void gameEvent(Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        }

        @Override
        public RegistryAccess registryAccess() {
            return this.world.registryAccess();
        }

        @Override
        public List<? extends Player> players() {
            return this.world.players();
        }

        @Override
        public Holder<Biome> getUncachedNoiseBiome(int p_46809_, int p_46810_, int p_46811_) {
            return this.world.getUncachedNoiseBiome(p_46809_, p_46810_, p_46811_);
        }

        @Override
        public float getShade(Direction p_45522_, boolean p_45523_) {
            return this.world.getShade(p_45522_, p_45523_);
        }

        @Override
        public void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_) {
        }

        @Override
        public String gatherChunkSourceStats() {
            return this.world.gatherChunkSourceStats();
        }

        @Override
        public Entity getEntity(int id) {
            return this.world.getEntity(id);
        }

        @Override
        public MapItemSavedData getMapData(String mapId) {
            return this.world.getMapData(mapId);
        }

        @Override
        public void setMapData(String mapId, MapItemSavedData mapData) {
        }

        @Override
        public int getFreeMapId() {
            return this.world.getFreeMapId();
        }

        @Override
        public void destroyBlockProgress(int entityId, BlockPos pos, int progress) {
        }

        @Override
        public Scoreboard getScoreboard() {
            return this.world.getScoreboard();
        }

        @Override
        public RecipeManager getRecipeManager() {
            return this.world.getRecipeManager();
        }

        @Override
        protected LevelEntityGetter<Entity> getEntities() {
            return this.entityStorage.getEntityGetter();
        }

        @Override
        public void gameEvent(GameEvent p_220404_, Vec3 p_220405_, GameEvent.Context p_220406_) {
        }

        @Override
        public void playSeededSound(Player p_220363_, double p_220364_, double p_220365_, double p_220366_, SoundEvent p_220367_, SoundSource p_220368_, float p_220369_, float p_220370_, long p_220371_) {
        }

        @Override
        public FeatureFlagSet enabledFeatures() {
            return this.world.enabledFeatures();
        }

        @Override
        public void playSeededSound(Player p_262953_, double p_263004_, double p_263398_, double p_263376_, Holder<SoundEvent> p_263359_, SoundSource p_263020_, float p_263055_, float p_262914_, long p_262991_) {
        }

        @Override
        public void playSeededSound(Player p_220372_, Entity p_220373_, Holder<SoundEvent> p_263500_, SoundSource p_220375_, float p_220376_, float p_220377_, long p_220378_) {
        }

        @OnlyIn(Dist.CLIENT)
        final class EntityCallbacks implements LevelCallback<Entity> {
            public void onCreated(Entity p_171696_) {
            }

            public void onDestroyed(Entity p_171700_) {
            }

            public void onTickingStart(Entity p_171704_) {
            }

            public void onTickingEnd(Entity p_171708_) {
            }

            public void onTrackingStart(Entity p_171712_) {
            }

            public void onTrackingEnd(Entity p_171716_) {
            }

            public void onSectionChange(Entity p_223609_) {
            }
        }
    }
}