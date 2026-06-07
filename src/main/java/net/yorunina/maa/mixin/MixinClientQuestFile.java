package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientQuestFile.class)
public abstract class MixinClientQuestFile extends BaseQuestFile {
    @Shadow
    public TeamData selfTeamData;

    @Inject(method = "getOrCreateTeamData", at = @At("HEAD"), cancellable = true, remap = false)
    public void getOrCreateTeamDataInject(Entity player, CallbackInfoReturnable<TeamData> cir) {
        cir.setReturnValue(player.getUUID().equals(Minecraft.getInstance().player.getUUID()) ? selfTeamData : this.getOrCreateTeamData(selfTeamData.getTeamId()));
    }

    @Inject(method = "isPlayerOnTeam", at = @At("HEAD"), cancellable = true, remap = false)
    public void isPlayerOnTeamInject(Player player, TeamData teamData, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(selfTeamData.getTeamId().equals(teamData.getTeamId()));
    }
}
