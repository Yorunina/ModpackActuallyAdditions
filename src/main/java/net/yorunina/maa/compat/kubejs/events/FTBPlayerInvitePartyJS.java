package net.yorunina.maa.compat.kubejs.events;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbteams.api.Team;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class FTBPlayerInvitePartyJS extends EventJS {
    public Team team;
    public ServerPlayer inviter;
    public Collection<GameProfile> profiles;
    public FTBPlayerInvitePartyJS(Team cur, ServerPlayer p, Collection<GameProfile> profiles) {
        super();
        this.team = cur;
        this.inviter = p;
        this.profiles = profiles;
    }

    public Team getTeam() {
        return team;
    }
    public ServerPlayer getInviter() {
        return inviter;
    }
    public Collection<GameProfile> getProfiles() {
        return profiles;
    }
}
