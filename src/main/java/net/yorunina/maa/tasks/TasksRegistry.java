package net.yorunina.maa.tasks;

import com.mojang.datafixers.util.Function3;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.api.QuestFile;
import dev.ftb.mods.ftbquests.events.ClearFileCacheEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.yorunina.maa.ModpackActuallyAdditions;
import net.yorunina.maa.rewards.KubeReward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class TasksRegistry {
    public static final TaskType ASTAGES = TaskTypes.register(ModpackActuallyAdditions.id("astages"), AStageTask::new, () -> Icons.CONTROLLER);
    public static final TaskType DIM_NET = TaskTypes.register(ModpackActuallyAdditions.id("dim_net"), DimNetTask::new, () -> Icons.CONTROLLER);
    public static final TaskType EAT_ITEM = TaskTypes.register(ModpackActuallyAdditions.id("eat_item"), EatItemTask::new, () -> Icons.CONTROLLER);
    public static final TaskType KUBE = TaskTypes.register(ModpackActuallyAdditions.id("kube"), KubeTask::new, () -> Icons.CONTROLLER);
    public static final TaskType KUBE_TEXT_INPUT = TaskTypes.register(ModpackActuallyAdditions.id("kube_text_input"), KubeTextInputTask::new, () -> Icons.CONTROLLER);


    private static TasksRegistry INSTANCE = null;

    private List<EatItemTask> eatItemTaskList = null;
    private Map<String, List<KubeTask>> kubeTaskMap = null;


    public TasksRegistry() {
    }

    public void init() {
        ClearFileCacheEvent.EVENT.register(this::fileCacheClear);
    }

    private void fileCacheClear(QuestFile file) {
        if (file.isServerSide()) {
            this.eatItemTaskList = null;
            this.kubeTaskMap = null;
        }
    }

    public static TasksRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRegistry();
        }
        return INSTANCE;
    }

    public void onLivingEntityEat(ServerPlayer player, ItemStack food) {
        if (this.eatItemTaskList == null) {
            this.eatItemTaskList = ServerQuestFile.INSTANCE.collect(EatItemTask.class);
        }
        if (this.eatItemTaskList.isEmpty()) return;
        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
        if (team == null) return;
        TeamData teamData = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
        for (EatItemTask task : this.eatItemTaskList) {
            if (teamData.getProgress(task) < task.getMaxProgress() && teamData.canStartTasks(task.getQuest())) {
                task.eat(teamData, player, food);
            }
        }
    }

    public void onKubeTaskFinish(List<String> kubeIds, ServerPlayer player, Function3<KubeTask, ServerPlayer, TeamData, Void> consumer) {
        if (this.kubeTaskMap == null) {
            this.kubeTaskMap = new HashMap<>();
            for (KubeTask kubeTask : ServerQuestFile.INSTANCE.collect(KubeTask.class)) {
                this.kubeTaskMap.computeIfAbsent(kubeTask.getKubeId(), k -> new ArrayList<>()).add(kubeTask);
            }
        }
        if (this.kubeTaskMap.isEmpty()) return;
        List<KubeTask> kubeTasks = new ArrayList<>();
        for (String kubeId : kubeIds) {
            if (!this.kubeTaskMap.containsKey(kubeId)) continue;
            kubeTasks.addAll(this.kubeTaskMap.get(kubeId));
        }
        if (kubeTasks.isEmpty()) return;
        Team team = TeamManagerImpl.INSTANCE.getTeamForPlayer(player).orElse(null);
        if (team == null) return;
        TeamData teamData = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
        kubeTasks.stream()
                .filter(kubeTask -> teamData.getProgress(kubeTask) < kubeTask.getMaxProgress() && teamData.canStartTasks(kubeTask.getQuest()))
                .forEach(kubeTask -> consumer.apply(kubeTask, player, teamData));
    }
}
