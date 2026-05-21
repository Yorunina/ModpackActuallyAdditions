package net.yorunina.maa.networks;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.yorunina.maa.compat.kubejs.MAAUtils;

public class SyncEternalWinterMessage extends BaseS2CMessage {
    private final boolean isEternalWinter;


    public SyncEternalWinterMessage(FriendlyByteBuf buffer) {
        isEternalWinter = buffer.readBoolean();
    }

    public SyncEternalWinterMessage(boolean value) {
        isEternalWinter = value;
    }

    public MessageType getType() {
        return MAAQuestNetHandler.SYNC_ETERNAL_WINTER;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isEternalWinter);
    }

    public void handle(NetworkManager.PacketContext context) {
        MAAUtils.INSTANCE.eternalWinterEnabled = this.isEternalWinter;
    }
}
