package net.yorunina.ftbquestactuallyadditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yorunina.ftbquestactuallyadditions.rewards.AARewardTypes;
import net.yorunina.ftbquestactuallyadditions.tasks.AATaskTypes;

@Mod(FTBQuestActuallyAdditions.MODID)
public class FTBQuestActuallyAdditions {

    public static final String MODID = "ftbqaa";

    public FTBQuestActuallyAdditions(FMLJavaModLoadingContext context) {
        AARewardTypes.init();
        AATaskTypes.init();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}
