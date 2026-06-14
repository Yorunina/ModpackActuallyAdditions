package net.yorunina.maa.networks;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.yorunina.maa.client.SelectorScreenHandler;

import java.util.ArrayList;
import java.util.List;

public class SyncRegistryEntriesMessage extends BaseS2CMessage {
    private final String registryType;
    private final List<ResourceLocation> entries;

    public SyncRegistryEntriesMessage(FriendlyByteBuf buffer) {
        this.registryType = buffer.readUtf();
        int size = buffer.readVarInt();
        this.entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.entries.add(buffer.readResourceLocation());
        }
    }

    public SyncRegistryEntriesMessage(String registryType, List<ResourceLocation> entries) {
        this.registryType = registryType;
        this.entries = entries;
    }

    @Override
    public MessageType getType() {
        return MAAQuestNetHandler.SYNC_REGISTRY_ENTRIES;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.registryType);
        buffer.writeVarInt(this.entries.size());
        for (ResourceLocation entry : this.entries) {
            buffer.writeResourceLocation(entry);
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        RegistryEntriesHelper.setClientCache(registryType, entries);
        SelectorScreenHandler.onRegistryEntriesReceived(registryType);
    }
}