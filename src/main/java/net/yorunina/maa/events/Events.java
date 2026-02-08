package net.yorunina.maa.events;

import com.alessandro.astages.event.custom.LivingEntityEatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.tasks.TasksRegistry;

@Mod.EventBusSubscriber(modid = ModpackActuallyAdditions.MODID)
public class Events {
    @SubscribeEvent
    public static void onLivingEntityEat(LivingEntityEatEvent event) {
        if (event.getPlayer() == null) return;
        TasksRegistry.getInstance().onLivingEntityEat(event.getPlayer(), event.getFood());
    }
}
