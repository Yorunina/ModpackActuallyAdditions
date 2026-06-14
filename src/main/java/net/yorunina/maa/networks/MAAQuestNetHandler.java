package net.yorunina.maa.networks;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.yorunina.maa.ModpackActuallyAdditions;

public interface MAAQuestNetHandler {
    SimpleNetworkManager NET = SimpleNetworkManager.create(ModpackActuallyAdditions.MODID);
    MessageType KUBE_TEXT_INPUT_SUBMIT = NET.registerC2S("kube_text_input_submit", KubeTextInputSubmitMessage::new);
    MessageType SYNC_ETERNAL_WINTER = NET.registerS2C("sync_eternal_winter", SyncEternalWinterMessage::new);
    MessageType SYNC_REPEAT_TASK_COMPLETED = NET.registerS2C("sync_repeat_task_completed", SyncRepeatTaskCompletedMessage::new);
    MessageType SYNC_SELECTOR_ENTRY = NET.registerC2S("sync_selector_entry", SyncSelectorEntryMessage::new);
    MessageType REQUEST_REGISTRY_ENTRIES = NET.registerC2S("request_registry_entries", RequestRegistryEntriesMessage::new);
    MessageType SYNC_REGISTRY_ENTRIES = NET.registerS2C("sync_registry_entries", SyncRegistryEntriesMessage::new);
    static void init() {}
}