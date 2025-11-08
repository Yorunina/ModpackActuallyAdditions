package net.yorunina.ftbquestactuallyadditions.tasks;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.yorunina.ftbquestactuallyadditions.FTBQuestActuallyAdditions;
import net.yorunina.ftbquestactuallyadditions.rewards.AStageReward;

public interface AATaskTypes {
    TaskType ASTAGE = TaskTypes.register(FTBQuestActuallyAdditions.id( "astages"), AStageTask::new, () -> Icons.CONTROLLER);
    static void init() {}
}
