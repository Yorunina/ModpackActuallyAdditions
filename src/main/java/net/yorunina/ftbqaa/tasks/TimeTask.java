package net.yorunina.ftbqaa.tasks;


import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.yorunina.ftbqaa.consts.TimeMatch;
import dev.ftb.mods.ftblibrary.ui.Button;

import java.awt.*;
import java.util.Date;

public class TimeTask extends Task {
    long elapsedTime = 60L;
    TimeMatch gameTime;

    public TimeTask(long id, Quest quest) {
        super(id, quest);
        this.gameTime = TimeMatch.AUTO;
    }

    public TaskType getType() {
        return TasksRegistry.TIME;
    }

    public long getMaxProgress() {
        return Long.MAX_VALUE;
    }

    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putLong("elapsedTime", this.elapsedTime);
        if (this.gameTime != TimeMatch.AUTO) {
            nbt.putString("gameTime", this.gameTime.name);
        }

    }

    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        this.elapsedTime = nbt.getLong("elapsedTime");
        this.gameTime = TimeMatch.NAME_MAP.get(nbt.getString("gameTime"));
    }

    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeLong(this.elapsedTime);
        TimeMatch.NAME_MAP.write(buffer, this.gameTime);
    }

    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.elapsedTime = buffer.readLong();
        this.gameTime = TimeMatch.NAME_MAP.read(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addLong("elapsedTime", this.elapsedTime, (v) -> this.elapsedTime = v, 60L, 1L, Long.MAX_VALUE);
        config.addEnum("gameTime", this.gameTime, (v) -> this.gameTime = v, TimeMatch.NAME_MAP, TimeMatch.AUTO);
    }

    @OnlyIn(Dist.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
    }

    @OnlyIn(Dist.CLIENT)
    public MutableComponent getButtonText() {
        return (MutableComponent) CommonComponents.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        list.reset();
        list.add(this.getTitle());
        if (!teamData.isCompleted(this) && teamData.getProgress(this) != 0L) {
            list.blankLine();
            list.add(this.timeRemaining(teamData).withStyle(ChatFormatting.YELLOW));
        }

    }

    @OnlyIn(Dist.CLIENT)
    private MutableComponent timeRemaining(TeamData teamData) {
        int current_time = (int)(teamData.getProgress(this) + 20L - (this.useGameTime(!Minecraft.getInstance().isLocalServer()) ? Minecraft.getInstance().level.getGameTime() : System.currentTimeMillis() / 50L));
        return Component.translatable("ftbquests.task.time_remaining", new Object[]{StringUtil.formatTickDuration(current_time)});
    }

    private boolean useGameTime(boolean isServer) {
        return this.gameTime == TimeMatch.AUTO ? isServer : this.gameTime == TimeMatch.GAME;
    }

    public int autoSubmitOnPlayerTick() {
        return 10;
    }

    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (!teamData.isCompleted(this)) {
            long current_time = this.useGameTime(player.serverLevel().isClientSide()) ? player.level().getGameTime() : System.currentTimeMillis() / 50L;
            if (teamData.getProgress(this) == 0L) {
                teamData.setProgress(this, current_time + this.elapsedTime * 20L);
                teamData.setStarted(this.id, (Date)null);
            } else if (current_time > teamData.getProgress(this)) {
                teamData.setProgress(this, this.getMaxProgress());
            }
        }

    }
}

