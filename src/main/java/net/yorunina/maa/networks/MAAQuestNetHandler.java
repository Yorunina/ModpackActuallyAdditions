package net.yorunina.maa.networks;

import dev.architectury.networking.simple.MessageType;

import static dev.ftb.mods.ftbquests.net.FTBQuestsNetHandler.NET;

public interface MAAQuestNetHandler {
    MessageType KUBE_TEXT_INPUT_SUBMIT = NET.registerC2S("kube_text_input_submit", KubeTextInputSubmitMessage::new);
}
