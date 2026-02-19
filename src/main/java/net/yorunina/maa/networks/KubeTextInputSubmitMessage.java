package net.yorunina.maa.networks;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.yorunina.maa.compat.kubejs.events.TextInputTaskSubmitJS;

import static net.yorunina.maa.compat.kubejs.MAAEvents.TEXT_INPUT_TASK_SUBMIT;


public class KubeTextInputSubmitMessage extends BaseC2SMessage {
    private final long taskId;
    private final String kubeId;
    private final String inputText;


    public KubeTextInputSubmitMessage(FriendlyByteBuf buffer) {
        this.taskId = buffer.readLong();
        this.kubeId = buffer.readUtf();
        this.inputText = buffer.readUtf();
    }

    public KubeTextInputSubmitMessage(long taskId, String kubeId, String inputText) {
        this.taskId = taskId;
        this.kubeId = kubeId;
        this.inputText = inputText;
    }

    public MessageType getType() {
        return MAAQuestNetHandler.KUBE_TEXT_INPUT_SUBMIT;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(this.taskId);
        buffer.writeUtf(this.kubeId);
        buffer.writeUtf(this.inputText);
    }

    public void handle(NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer)context.getPlayer();
        TeamData data = TeamData.get(player);
        if (!data.isLocked()) {
            Task task = data.getFile().getTask(this.taskId);
            if (task != null) {
                BaseQuestFile var6 = data.getFile();
                if (var6 instanceof ServerQuestFile sqf) {
                    if (data.canStartTasks(task.getQuest()) && !data.isCompleted(task.getQuest())) {
                        sqf.withPlayerContext(player, () -> {
                            TEXT_INPUT_TASK_SUBMIT.post(new TextInputTaskSubmitJS(task, player, data, inputText), kubeId);
                        });
                    }
                }
            }
        }

    }
}