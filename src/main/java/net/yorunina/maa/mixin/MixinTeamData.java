package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.util.QuestKey;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.server.level.ServerPlayer;
import net.yorunina.maa.model.ITeamData;
import net.yorunina.maa.registry.MAAGameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(value = TeamData.class)
public abstract class MixinTeamData implements ITeamData {
    @Shadow
    @Final
    private Long2LongMap completed;
    @Shadow
    @Final
    private Long2LongMap started;
    @Shadow
    @Final
    private Object2LongMap<QuestKey> claimedRewards;
    @Shadow
    private boolean locked;
    @Shadow
    @Final
    private BaseQuestFile file;
    @Shadow
    @Final
    private UUID teamId;

    @Shadow
    public abstract boolean isRewardBlocked(Reward reward);

    @Shadow
    public abstract void markDirty();

    @Shadow
    public abstract void clearCachedProgress();

    @Unique
    public boolean isCompletedById(String id) {
        return this.completed.containsKey(Long.parseLong(id, 16));
    }

    @Unique
    public boolean isStartedById(String id) {
        return this.started.containsKey(Long.parseLong(id, 16));
    }

    @Unique
    public boolean markRewardAsClaimedNoRepeat(UUID player, Reward reward, long date) {
        if (locked || isRewardBlocked(reward)) {
            return false;
        }
        QuestKey key = QuestKey.forReward(player, reward);

        if (!claimedRewards.containsKey(key)) {
            claimedRewards.put(key, date);
            clearCachedProgress();
            markDirty();
            return true;
        }
        return false;
    }

    @Inject(method = "getOnlineMembers", at = @At("HEAD"), cancellable = true, remap = false)
    public void getOnlineMembersInject(CallbackInfoReturnable<Collection<ServerPlayer>> cir) {
        if (file.isServerSide() && file instanceof ServerQuestFile sqf) {
            if (!sqf.server.getGameRules().getBoolean(MAAGameRules.SHARE_TEAM_PROGRESS)) {
                ServerPlayer player = sqf.server.getPlayerList().getPlayer(teamId);
                cir.setReturnValue(player != null ? List.of(player) : List.of());
                return;
            }
            cir.setReturnValue(FTBTeamsAPI.api().getManager().getTeamByID(teamId)
                    .map(Team::getOnlineMembers)
                    .orElse(List.of()));
            return;
        }
        cir.setReturnValue(List.of());
    }
}