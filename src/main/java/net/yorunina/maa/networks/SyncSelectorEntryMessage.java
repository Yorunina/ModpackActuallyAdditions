package net.yorunina.maa.networks;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.yorunina.maa.items.BaseSelectorItem;

public class SyncSelectorEntryMessage extends BaseC2SMessage {
    private final int slotIndex;
    private final boolean isOffHand;
    private final ResourceLocation selectedEntry;

    public SyncSelectorEntryMessage(FriendlyByteBuf buffer) {
        this.slotIndex = buffer.readVarInt();
        this.isOffHand = buffer.readBoolean();
        this.selectedEntry = buffer.readResourceLocation();
    }

    public SyncSelectorEntryMessage(int slotIndex, boolean isOffHand, ResourceLocation selectedEntry) {
        this.slotIndex = slotIndex;
        this.isOffHand = isOffHand;
        this.selectedEntry = selectedEntry;
    }

    @Override
    public MessageType getType() {
        return MAAQuestNetHandler.SYNC_SELECTOR_ENTRY;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.slotIndex);
        buffer.writeBoolean(this.isOffHand);
        buffer.writeResourceLocation(this.selectedEntry);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (player == null) return;

        ItemStack stack;
        if (isOffHand) {
            stack = player.getOffhandItem();
        } else {
            if (slotIndex < 0 || slotIndex >= player.getInventory().items.size()) {
                return;
            }
            stack = player.getInventory().getItem(slotIndex);
        }

        if (!(stack.getItem() instanceof BaseSelectorItem selector)) {
            return;
        }

        if (!selector.isValidEntry(selectedEntry)) {
            return;
        }

        selector.setSelectedEntry(stack, selectedEntry);
    }
}