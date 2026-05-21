package net.yorunina.maa.networks;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftblibrary.util.client.ClientUtils;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import net.minecraft.network.FriendlyByteBuf;
import net.yorunina.maa.model.ITeamData;

import java.util.UUID;

public class SyncRepeatTaskCompletedMessage extends BaseS2CMessage {
    private final long rewardId;
    private final UUID playerId;
    private final UUID teamId;

    public SyncRepeatTaskCompletedMessage(FriendlyByteBuf buffer) {
        this.rewardId = buffer.readLong();
        this.playerId = buffer.readUUID();
        this.teamId = buffer.readUUID();
    }

    public SyncRepeatTaskCompletedMessage(UUID teamId, UUID playerId, long rewardId ) {
        this.rewardId = rewardId;
        this.playerId = playerId;
        this.teamId = teamId;
    }

    public MessageType getType() {
        return MAAQuestNetHandler.SYNC_REPEAT_TASK_COMPLETED;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(this.rewardId);
        buffer.writeUUID(this.playerId);
        buffer.writeUUID(this.teamId);
    }

    public void handle(NetworkManager.PacketContext context) {
        Reward reward = ClientQuestFile.INSTANCE.getReward(rewardId);

        if (reward == null) {
            return;
        }

        TeamData data = ClientQuestFile.INSTANCE.getOrCreateTeamData(teamId);
        ((ITeamData)data).markRewardAsClaimedNoRepeat(playerId, reward, System.currentTimeMillis());

        if (data == ClientQuestFile.INSTANCE.selfTeamData) {
            QuestScreen treeGui = ClientUtils.getCurrentGuiAs(QuestScreen.class);
            if (treeGui != null) {
                treeGui.refreshViewQuestPanel();
                treeGui.otherButtonsTopPanel.refreshWidgets();
            }
        }
    }
}
