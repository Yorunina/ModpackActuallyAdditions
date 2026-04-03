package net.yorunina.maa.compat.kubejs;


import com.mojang.datafixers.util.Function3;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.dimensionnet.NetRegistryIndex;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.util.ProgressChange;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.yorunina.maa.tasks.KubeTask;
import net.yorunina.maa.tasks.TasksRegistry;

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
        ProgressChange change = new ProgressChange(ServerQuestFile.INSTANCE, teamData.getFile(), FTBQuestsClient.getClientPlayer().getUUID());
        change.setReset(true);
        change.maybeForceProgress(teamData.getTeamId());
    }

    public void resetServerTaskProgress(MinecraftServer server) {
        ServerQuestFile.INSTANCE.getAllTeamData().forEach(teamData -> {
            ProgressChange change = new ProgressChange(ServerQuestFile.INSTANCE, teamData.getFile(), FTBQuestsClient.getClientPlayer().getUUID());
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

    public void setEternalWinterEnabled(boolean enabled) {
        eternalWinterEnabled = enabled;
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
}
