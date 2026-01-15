package net.yorunina.ftbqaa.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public class PacketHandler {
    public static final SimpleNetworkManager INSTANCE = SimpleNetworkManager.create("questsadditions");
    public static MessageType SUBMIT_STRUCTURE;

    public static void init() {
        SUBMIT_STRUCTURE = INSTANCE.registerC2S("submit_structure", SubmitStructurePacket::new);
    }
}
