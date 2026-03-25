package net.yorunina.maa;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.yorunina.maa.client.entities.ClientRenderRegistry;
import net.yorunina.maa.entities.MAAEntityRegistry;
import net.yorunina.maa.items.ItemFiltersItems;
import net.yorunina.maa.rewards.AARewardTypes;
import net.yorunina.maa.tasks.TasksRegistry;

@Mod(ModpackActuallyAdditions.MODID)
public class ModpackActuallyAdditions {
    public static final String MODID = "maa";
    public ModpackActuallyAdditions(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonInit);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::clientInit);
        }

        AARewardTypes.init();
        ItemFiltersItems.register(modEventBus);
        MAAEntityRegistry.Defer.register(modEventBus);
    }

    private void commonInit(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TasksRegistry.getInstance().init();
        });
    }

    private void clientInit(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientRenderRegistry.init();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}