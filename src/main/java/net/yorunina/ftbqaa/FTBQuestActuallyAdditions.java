package net.yorunina.ftbqaa;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yorunina.ftbqaa.items.ItemFiltersItems;
import net.yorunina.ftbqaa.network.PacketHandler;
import net.yorunina.ftbqaa.rewards.AARewardTypes;
import net.yorunina.ftbqaa.tasks.TasksRegistry;

@Mod(FTBQuestActuallyAdditions.MODID)
public class FTBQuestActuallyAdditions {

    public static final String MODID = "ftbqaa";

    public FTBQuestActuallyAdditions(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::preInit);
        IEventBus eventBus = context.getModEventBus();
        AARewardTypes.init();
        ItemFiltersItems.register(eventBus);
    }

    private void preInit(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TasksRegistry.getInstance().init();
            PacketHandler.init();
        });
    }
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}
