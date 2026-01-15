package net.yorunina.ftbqaa.client;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.yorunina.ftbqaa.FTBQuestActuallyAdditions;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(
        modid = FTBQuestActuallyAdditions.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientConfigs {
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static boolean mouseZoom;

    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == CLIENT_SPEC) {
            mouseZoom = (Boolean)CLIENT.mouseZoom.get();
        }

    }

    static {
        Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = (new ForgeConfigSpec.Builder()).configure(ClientConfig::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }

    public static class ClientConfig {
        public final ForgeConfigSpec.BooleanValue mouseZoom;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Configs");
            this.mouseZoom = builder.comment(new String[]{"Set to true to have the zoom centered on your mouse position", "instead of the center of the screen."}).define("mouse_zoom", false);
            builder.pop();
        }
    }
}
