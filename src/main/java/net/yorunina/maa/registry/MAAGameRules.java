package net.yorunina.maa.registry;

import dev.ftb.mods.ftbquests.net.SyncTeamDataMessage;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

import java.util.UUID;

public class MAAGameRules {
    private static boolean previousValue = false;

    public static final GameRules.Key<GameRules.BooleanValue> SHARE_TEAM_PROGRESS = GameRules.register(
            "FTBQShareTeamProgress",
            GameRules.Category.PLAYER,
            GameRules.BooleanValue.create(false, (server, value) -> {
                boolean newValue = value.get();
                if (previousValue != newValue) {
                    if (!previousValue) {
                        mergeAllPlayerProgressToTeam(server);
                    }
                    syncAllTeamDataToClients(server);
                }
                previousValue = newValue;
            })
    );
    public static void init() {}

    public static void onServerLoaded(MinecraftServer server) {
        previousValue = server.getGameRules().getBoolean(SHARE_TEAM_PROGRESS);
    }

    private static void syncAllTeamDataToClients(MinecraftServer server) {
        if (ServerQuestFile.INSTANCE == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            new SyncTeamDataMessage(ServerQuestFile.INSTANCE.getOrCreateTeamData(player), true).sendTo(player);
        }
    }

    private static void mergeAllPlayerProgressToTeam(MinecraftServer server) {
        if (ServerQuestFile.INSTANCE == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            mergePlayerProgressToTeam(player.getUUID());
        }

        ServerQuestFile.INSTANCE.markDirty();
    }

    private static void mergePlayerProgressToTeam(UUID playerUuid) {
        TeamData playerData = ServerQuestFile.INSTANCE.getOrCreateTeamData(playerUuid);

        FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUuid).ifPresent(team -> {
            UUID teamId = team.getId();
            if (!teamId.equals(playerUuid)) {
                ServerQuestFile.INSTANCE.getOrCreateTeamData(teamId).mergeData(playerData);
            }
        });
    }
}
