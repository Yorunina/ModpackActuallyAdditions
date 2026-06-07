package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.yorunina.maa.registry.MAAGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BaseQuestFile.class)
public abstract class MixinBaseQuestFile {
    @Shadow
    public abstract TeamData getOrCreateTeamData(UUID teamId);

    @Inject(method = "getOrCreateTeamData(Lnet/minecraft/world/entity/Entity;)Ldev/ftb/mods/ftbquests/quest/TeamData;", at = @At("HEAD"), cancellable = true, remap = false)
    public void getOrCreateTeamDataInject(Entity player, CallbackInfoReturnable<TeamData> cir) {
        cir.setReturnValue(getOrCreateTeamData(getTeamIdForPlayer(player)));
    }

    @Unique
    protected UUID getTeamIdForPlayer(Entity player) {
        if (player instanceof ServerPlayer sp && !sp.server.getGameRules().getBoolean(MAAGameRules.SHARE_TEAM_PROGRESS)) {
            return player.getUUID();
        }
        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(player.getUUID())
                .map(Team::getId)
                .orElse(player.getUUID());
    }
}
