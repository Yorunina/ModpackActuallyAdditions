package net.yorunina.maa.mixin;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.yorunina.maa.compat.kubejs.events.FTBCreatePartyJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static net.yorunina.maa.compat.kubejs.MAAEvents.FTB_CREATE_PARTY;

@Mixin(TeamManagerImpl.class)
public class MixinTeamManagerImpl {
    @Inject(
            method = {"createParty(Ljava/util/UUID;Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;Ljava/lang/String;Ldev/ftb/mods/ftblibrary/icon/Color4I;)Ldev/ftb/mods/ftbteams/data/PartyTeam;"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    public void createParty(UUID playerId, ServerPlayer player, String name, String description, Color4I color, CallbackInfoReturnable<PartyTeam> cir) {
        FTBCreatePartyJS event = new FTBCreatePartyJS(playerId, player, name, description, color);
        if (FTB_CREATE_PARTY.post(event).arch().isFalse()) {
            // 抛出exception
            cir.cancel();
        }
    }
}
