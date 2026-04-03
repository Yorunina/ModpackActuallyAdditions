package net.yorunina.maa.events;

import com.alessandro.astages.event.custom.LivingEntityEatEvent;
import com.tom.createores.block.DrillBlock;
import com.tom.createores.block.ExtractorBlock;
import com.tom.createores.block.entity.DrillBlockEntity;
import com.tom.createores.block.entity.ExtractorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.compat.kubejs.MAAUtils;
import net.yorunina.maa.model.IDrillBlock;
import net.yorunina.maa.tasks.TasksRegistry;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID)
public class Events {
    @SubscribeEvent
    public static void onLivingEntityEat(LivingEntityEatEvent event) {
        if (event.getPlayer() == null) return;
        TasksRegistry.getInstance().onLivingEntityEat(event.getPlayer(), event.getFood());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MAAUtils.INSTANCE.resetInstance();
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        BlockState blockState = event.getPlacedBlock();
        LevelAccessor pLevel = event.getLevel();
        BlockPos pPos = event.getPos();
        Entity pPlacer = event.getEntity();
        if (!(pPlacer instanceof Player)) return;

        Block block = blockState.getBlock();
        if (block instanceof DrillBlock drillBlock) {
            DrillBlockEntity entity = drillBlock.getBlockEntity(pLevel, pPos);
            if (entity != null) {
                ((IDrillBlock) entity).setOwner(pPlacer.getUUID());
            }
        }
        if (block instanceof ExtractorBlock extractorBlock) {
            ExtractorBlockEntity entity = extractorBlock.getBlockEntity(pLevel, pPos);
            if (entity != null) {
                ((IDrillBlock) entity).setOwner(pPlacer.getUUID());
            }
        }
    }
}
