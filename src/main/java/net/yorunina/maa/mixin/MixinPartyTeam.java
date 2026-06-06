package net.yorunina.maa.mixin;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import net.minecraft.server.level.ServerPlayer;
import net.yorunina.maa.compat.kubejs.events.FTBPlayerInvitePartyJS;
import net.yorunina.maa.compat.kubejs.events.FTBPlayerJoinPartyJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

import static net.yorunina.maa.compat.kubejs.MAAEvents.FTB_PLAYER_INVITE_PARTY_TEAM;
import static net.yorunina.maa.compat.kubejs.MAAEvents.FTB_PLAYER_JOIN_PARTY_TEAM;

@Mixin(PartyTeam.class)
public class MixinPartyTeam {
    @Inject(method = "join",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    public void join(ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
        FTBPlayerJoinPartyJS event = new FTBPlayerJoinPartyJS((Team) this, player);
        if (FTB_PLAYER_JOIN_PARTY_TEAM.post(event).arch().isFalse()) {
            // 抛出exception
            cir.cancel();
        }
    }

    @Inject(method = "invite",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    public void invite(ServerPlayer inviter, Collection<GameProfile> profiles, CallbackInfoReturnable<Integer> cir) {
        FTBPlayerInvitePartyJS event = new FTBPlayerInvitePartyJS((Team) this, inviter, profiles);
        if (FTB_PLAYER_INVITE_PARTY_TEAM.post(event).arch().isFalse()) {
            // 抛出exception
            cir.cancel();
        }
    }
}
