package net.yorunina.maa.compat.kubejs;


import com.mojang.datafixers.util.Function3;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.dimensionnet.NetRegistryIndex;
import dev.ftb.mods.ftbquests.net.ClaimRewardResponseMessage;
import dev.ftb.mods.ftbquests.net.ObjectCompletedMessage;
import dev.ftb.mods.ftbquests.net.UpdateTaskProgressMessage;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.util.ProgressChange;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.yorunina.maa.networks.SyncEternalWinterMessage;
import net.yorunina.maa.networks.SyncRepeatTaskCompletedMessage;
import net.yorunina.maa.tasks.KubeTask;
import net.yorunina.maa.tasks.TasksRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class MAAUtils {
    public static final MAAUtils INSTANCE = new MAAUtils();
    public boolean eternalWinterEnabled = false;
    public float globalTemperature = -10;
    public boolean noFireRender = false;

    private MAAUtils() {
    }

    public void resetInstance() {
        eternalWinterEnabled = false;
        globalTemperature = -10;
        noFireRender = false;
    }

    public void resetPlayerTaskProgress(Player player) {
        if (player == null) return;
        TeamData teamData = ServerQuestFile.INSTANCE.getOrCreateTeamData(player);
        ProgressChange change = new ProgressChange(ServerQuestFile.INSTANCE, teamData.getFile(), player.getUUID());
        change.setReset(true);
        change.maybeForceProgress(teamData.getTeamId());
    }

    public void resetServerTaskProgress(MinecraftServer server) {
        ServerQuestFile.INSTANCE.getAllTeamData().forEach(teamData -> {
            ProgressChange change = new ProgressChange(ServerQuestFile.INSTANCE, teamData.getFile(), teamData.getTeamId());
            change.setReset(true);
            change.maybeForceProgress(teamData.getTeamId());
        });
    }

    public void onKubeTaskFinish(String taskId, ServerPlayer player, Function3<KubeTask, ServerPlayer, TeamData, Void> consumer) {
        TasksRegistry.getInstance().onKubeTaskFinish(Collections.singletonList(taskId), player, consumer);
    }

    public void onKubeTasksFinish(List<String> taskIds, ServerPlayer player, Function3<KubeTask, ServerPlayer, TeamData, Void> consumer) {
        TasksRegistry.getInstance().onKubeTaskFinish(taskIds, player, consumer);
    }

    public void setNoFireRender(boolean value) {
        this.noFireRender = value;
    }


    public void setGlobalTemperature(float temperature) {
        globalTemperature = temperature;
    }

    public float getGlobalTemperature() {
        return globalTemperature;
    }

    public void setEternalWinterEnabled(MinecraftServer server, boolean enabled) {
        eternalWinterEnabled = enabled;
        new SyncEternalWinterMessage(enabled).sendToAll(server);
    }

    public void syncEternalWinterToPlayer(ServerPlayer player) {
        new SyncEternalWinterMessage(eternalWinterEnabled).sendTo(player);
    }

    public boolean shouldSnowContinuously() {
        return eternalWinterEnabled;
    }

    public String getChapterIdString(Chapter chapter) {
        return Long.toString(chapter.getId(), 16);
    }

    public void setTeamTaskCompleted(TeamData teamData, String taskId) {
        teamData.setCompleted(Long.parseLong(taskId, 16), new Date());
    }

    public Task getTaskByTeamData(TeamData teamData, String taskId) {
        return teamData.getFile().getTask(Long.parseLong(taskId, 16));
    }

    public List<DimensionsNet> getAllDimNet(MinecraftServer server) {
        if (server == null) return List.of();
        List<DimensionsNet> nets = new ArrayList<>();
        for (int netId : NetRegistryIndex.get(server).getActiveNetIds(server)) {
            DimensionsNet net = DimensionsNet.getNetFromId(netId);
            if (net != null) {
                nets.add(net);
            }
        }
        return nets;
    }

    public int repairPlayerItems(Player player, int remainingExp, int initialValue) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, player, ItemStack::isDamaged);
        if (entry != null) {
            ItemStack itemstack = entry.getValue();
            int i = Math.min((int) (initialValue * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - i);
            int j = remainingExp - i / 2;
            return j > 0 ? this.repairPlayerItems(player, j, initialValue) : 0;
        } else {
            return remainingExp;
        }
    }

    public DimensionsNet createBDNetForPlayer(ServerPlayer target, @Nullable Long slotCapacityOverride, @Nullable Integer slotMaxSizeOverride)
    {
        long slotCapacity = (slotCapacityOverride != null) ? slotCapacityOverride : Long.MAX_VALUE;
        int slotMaxSize = (slotMaxSizeOverride != null) ? slotMaxSizeOverride : Integer.MAX_VALUE;
        return DimensionsNet.createNewNetForPlayer(target, slotCapacity, slotMaxSize);
    }

    public void sendClientRepeatTaskCompleted(TeamData teamData, String taskId) {
        new ObjectCompletedMessage(teamData.getTeamId(), Long.parseLong(taskId, 16)).sendTo(teamData.getOnlineMembers());
    }
    public void sendClientRepeatTaskClaimed(TeamData teamData, UUID playerId, String rewardId) {
        new SyncRepeatTaskCompletedMessage(teamData.getTeamId(), playerId, Long.parseLong(rewardId, 16)).sendTo(teamData.getOnlineMembers());
    }
}