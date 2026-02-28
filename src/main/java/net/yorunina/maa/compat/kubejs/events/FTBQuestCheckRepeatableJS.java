package net.yorunina.maa.compat.kubejs.events;

import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.latvian.mods.kubejs.event.EventJS;

import java.util.UUID;

public class FTBQuestCheckRepeatableJS extends EventJS {
    public TeamData teamData;
    public UUID playerUUID;
    public String id;

    public FTBQuestCheckRepeatableJS(TeamData data, UUID player, String id) {
        super();
        this.teamData = data;
        this.playerUUID = player;
        this.id = id;
    }

    public TeamData getTeamData() {
        return teamData;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getId() {
        return id;
    }
}
