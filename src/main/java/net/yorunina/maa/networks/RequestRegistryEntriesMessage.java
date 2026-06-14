package net.yorunina.maa.networks;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class RequestRegistryEntriesMessage extends BaseC2SMessage {
    private final String registryType;

    public RequestRegistryEntriesMessage(FriendlyByteBuf buffer) {
        this.registryType = buffer.readUtf();
    }

    public RequestRegistryEntriesMessage(String registryType) {
        this.registryType = registryType;
    }

    @Override
    public MessageType getType() {
        return MAAQuestNetHandler.REQUEST_REGISTRY_ENTRIES;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.registryType);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (player == null) return;

        List<ResourceLocation> entries = RegistryEntriesHelper.getServerEntries(registryType, player);
        new SyncRegistryEntriesMessage(registryType, entries).sendTo(player);
    }
}