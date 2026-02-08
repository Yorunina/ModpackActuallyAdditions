package net.yorunina.maa.rewards;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardAutoClaim;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.server.command.CurioArgumentType;

import java.util.UUID;

public class CuriosSlotReward extends Reward {
    private String slot = "";
    private boolean isPermanent = true;
    private int count = 1;

    public CuriosSlotReward(long id, Quest quest) {
        super(id, quest);
        this.autoclaim = RewardAutoClaim.DEFAULT;
    }

    public RewardType getType() {
        return AARewardTypes.CURIOS_SLOT;
    }

    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putString("slot", this.slot);
        nbt.putBoolean("isPermanent", true);
        nbt.putInt("count", this.count);
    }

    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        this.slot = nbt.getString("slot");
        this.isPermanent = nbt.getBoolean("isPermanent");
        this.count = nbt.getInt("count");
    }

    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(this.slot, 32767);
        buffer.writeBoolean(this.isPermanent);
        buffer.writeInt(this.count);
    }

    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.slot = buffer.readUtf(32767);
        this.isPermanent = buffer.readBoolean();
        this.count = buffer.readInt();
    }

    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);

        config.addEnum("slot", this.slot, (v) -> {
            this.slot = v;
        }, NameMap.of("", CurioArgumentType.slotIds.stream().toList())
                .nameKey((v) -> "curios.identifier." + v).icon((v) -> Icons.CHAT).create(), "");
        config.addBool("isPermanent", this.isPermanent, (v) -> this.isPermanent = v, true);
        config.addInt("count", this.count, (v) -> this.count = v, 1, 0, Integer.MAX_VALUE);
    }

    public void claim(ServerPlayer player, boolean notify) {
        player.getCapability(CuriosCapability.INVENTORY).ifPresent(inv -> {
            if (this.isPermanent) {
                inv.addPermanentSlotModifier(this.slot, UUID.randomUUID(), "TaskSlotModifier", this.count, AttributeModifier.Operation.ADDITION);
            } else {
                inv.addTransientSlotModifier(this.slot, UUID.randomUUID(), "TaskSlotModifier", this.count, AttributeModifier.Operation.ADDITION);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public MutableComponent getAltTitle() {
        return Component.translatable("ftbquests.reward.maa.curios_slot").append(": ").append(Component.translatable("curios.identifier." + this.slot).withStyle(ChatFormatting.YELLOW)).append(" x ").append(Component.literal(Integer.toString(this.count)).withStyle(ChatFormatting.YELLOW));
    }

    public boolean ignoreRewardBlocking() {
        return true;
    }

    protected boolean isIgnoreRewardBlockingHardcoded() {
        return true;
    }
}