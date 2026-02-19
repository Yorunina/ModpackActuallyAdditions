package net.yorunina.maa.compat.kubejs.events;

import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.level.ServerPlayer;

public class TextInputTaskSubmitJS extends EventJS {
    public final Task task;
    public final String inputText;
    public final ServerPlayer player;
    public final TeamData teamData;

    public TextInputTaskSubmitJS(Task task, ServerPlayer player, TeamData data, String inputText) {
        super();
        this.task = task;
        this.inputText = inputText;
        this.player = player;
        this.teamData = data;
    }

    public String getInputText() {
        return inputText;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public TeamData getTeamData() {
        return teamData;
    }

    public Task getTask() {
        return task;
    }

}
