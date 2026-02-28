package net.yorunina.maa.compat.kubejs.events;

import dev.ftb.mods.ftbteams.api.Team;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.level.ServerPlayer;

public class FTBPlayerJoinPartyJS extends EventJS {
    public Team team;
    public ServerPlayer player;
    public FTBPlayerJoinPartyJS(Team cur, ServerPlayer p) {
        super();
        this.team = cur;
        this.player = p;
    }

    public Team getTeam() {
        return team;
    }
    public ServerPlayer getPlayer() {
        return player;
    }
}
