package net.yorunina.maa.compat.kubejs;


import com.mojang.datafixers.util.Function3;
import dev.ftb.mods.ftbquests.client.FTBQuestsClient;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.util.ProgressChange;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.mixin.MixinScreenEffectRenderer;
import net.yorunina.maa.tasks.KubeTask;
import net.yorunina.maa.tasks.TasksRegistry;

import java.util.Collections;
import java.util.List;

public class MAAUtils {
    public static final MAAUtils INSTANCE = new MAAUtils();
    public boolean noFireRender = false;

    public void resetPlayerTaskProgress(Player player) {
        if (player == null) return;
        TeamData teamData = ServerQuestFile.INSTANCE.getOrCreateTeamData(player);
        ProgressChange change = new ProgressChange(ServerQuestFile.INSTANCE, teamData.getFile(), FTBQuestsClient.getClientPlayer().getUUID());
        change.setReset(true);
        change.maybeForceProgress(teamData.getTeamId());
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
}
