package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbchunks.FTBChunks;
import dev.ftb.mods.ftbchunks.data.ChunkTeamDataImpl;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.event.PlayerLoggedInAfterTeamEvent;
import dev.ftb.mods.ftbteams.data.AbstractTeam;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FTBChunks.class)
public abstract class MixinFTBChunks {
    @Inject(method = "loggedIn", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbteams/api/Team;getOnlineMembers()Ljava/util/Collection;"), remap = false)
    private void loggedInInject(PlayerLoggedInAfterTeamEvent event, CallbackInfo ci, @Local(name = "data") ChunkTeamDataImpl data) {
        if (data.getTeam() instanceof AbstractTeam partyTeam) {
            if (partyTeam.getOnlineRanked(TeamRank.ALLY).size() == 1 && !data.canDoOfflineForceLoading()) {
                data.updateChunkTickets(true);
            }
        }
    }

    @Inject(method = "loggedOut", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbteams/api/Team;getOnlineMembers()Ljava/util/Collection;"), remap = false)
    private void loggedInInject(ServerPlayer player, CallbackInfo ci, @Local(name = "data") ChunkTeamDataImpl data) {
        if (data.getTeam() instanceof AbstractTeam partyTeam) {
            if (partyTeam.getOnlineRanked(TeamRank.ALLY).size() == 1 && !data.canDoOfflineForceLoading()) {
                data.updateChunkTickets(false);
            }
        }
    }
}
