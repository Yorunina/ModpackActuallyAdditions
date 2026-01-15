package net.yorunina.ftbqaa.client;


import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.client.Minecraft;


public class ClientRegistries {
    public static void register() {
        ClientLifecycleEvent.CLIENT_SETUP.register(ClientRegistries::registerRendering);
    }

    private static void registerRendering(Minecraft minecraft) {
    }
}

