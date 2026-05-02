package net.yorunina.maa.networks;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.yorunina.maa.ModpackActuallyAdditions;

public interface MAAQuestNetHandler {
    SimpleNetworkManager NET = SimpleNetworkManager.create(ModpackActuallyAdditions.MODID);
    MessageType KUBE_TEXT_INPUT_SUBMIT = NET.registerC2S("kube_text_input_submit", KubeTextInputSubmitMessage::new);
    public static void init() {
    }
}
