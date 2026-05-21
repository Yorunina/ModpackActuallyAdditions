package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.util.QuestKey;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.yorunina.maa.model.ITeamData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(value = TeamData.class, remap = false)
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
    public boolean isRewardBlocked(Reward reward) {
        return false;
    }
    @Shadow
    public void markDirty() {}
    @Shadow
    public void clearCachedProgress() {}

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

}