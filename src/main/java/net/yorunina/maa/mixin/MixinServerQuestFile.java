package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.event.PlayerChangedTeamEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.registry.MAAGameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerQuestFile.class)
public abstract class MixinServerQuestFile extends BaseQuestFile {
    @Shadow
    @Final
    public MinecraftServer server;
    @Redirect(method = "playerLoggedIn", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/ServerQuestFile;getOrCreateTeamData(Ldev/ftb/mods/ftbteams/api/Team;)Ldev/ftb/mods/ftbquests/quest/TeamData;"), remap = false)
    private TeamData getOrCreateTeamDataInject(ServerQuestFile serverQuestFile, Team team, @Local(name = "player") ServerPlayer player) {
        return this.getOrCreateTeamData(player);
    }

    @Inject(method = "playerChangedTeam", at = @At("HEAD"), cancellable = true, remap = false)
    private void playerChangedTeamInject(PlayerChangedTeamEvent event, CallbackInfo ci) {
        if (!server.getGameRules().getBoolean(MAAGameRules.SHARE_TEAM_PROGRESS)) {
            ci.cancel();
        }
    }

    @Inject(method = "isPlayerOnTeam", at = @At("HEAD"), cancellable = true, remap = false)
    private void isPlayerOnTeamInject(Player player, TeamData teamData, CallbackInfoReturnable<Boolean> cir) {
        if (!server.getGameRules().getBoolean(MAAGameRules.SHARE_TEAM_PROGRESS)) {
            cir.setReturnValue(player.getUUID().equals(teamData.getTeamId()));
        }
    }

    @Redirect(method = "lambda$isPlayerOnTeam$10", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbteams/api/Team;getTeamId()Ljava/util/UUID;"), remap = false)
    private static UUID isPlayerOnTeamGetTeamIdInject(Team team) {
        return team.getId();
    }
}
