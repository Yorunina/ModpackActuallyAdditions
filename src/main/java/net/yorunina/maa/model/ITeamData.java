package net.yorunina.maa.model;

import dev.ftb.mods.ftbquests.quest.reward.Reward;

import java.util.UUID;

public interface ITeamData {
    boolean markRewardAsClaimedNoRepeat(UUID player, Reward reward, long date);
}
