package net.yorunina.maa.compat.kubejs.events;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class FTBCreatePartyJS extends EventJS {
    public String name;
    public ServerPlayer player;
    public UUID playerId;
    public String description;
    public Color4I color;

    public FTBCreatePartyJS(UUID playerId, ServerPlayer player, String name, String description, Color4I color) {
        super();
        this.playerId = playerId;
        this.name = name;
        this.player = player;
        this.description = description;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getDescription() {
        return description;
    }

    public Color4I getColor() {
        return color;
    }
}
