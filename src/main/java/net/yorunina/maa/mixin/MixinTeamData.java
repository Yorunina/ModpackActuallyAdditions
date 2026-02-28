package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.quest.TeamData;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TeamData.class)
public class MixinTeamData {
    @Shadow
    @Final
    private Long2LongMap completed;

    @Shadow
    @Final
    private Long2LongMap started;

    @Unique
    public boolean isCompletedById(String id) {
        return this.completed.containsKey(Long.parseLong(id, 16));
    }
    @Unique
    public boolean isStartedById(String id) {
        return this.started.containsKey(Long.parseLong(id, 16));
    }
}
