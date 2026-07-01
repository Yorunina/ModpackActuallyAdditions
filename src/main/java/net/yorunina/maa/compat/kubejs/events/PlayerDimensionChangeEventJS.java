package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class PlayerDimensionChangeEventJS extends PlayerEventJS {
    private final ServerPlayer player;
    private final ResourceKey<Level> fromDimension;
    private final ResourceKey<Level> toDimension;
    private final Level level;

    public PlayerDimensionChangeEventJS(ServerPlayer player, ResourceKey<Level> fromDimension, ResourceKey<Level> toDimension) {
        super();
        this.player = player;
        this.level = player.level();
        this.fromDimension = fromDimension;
        this.toDimension = toDimension;
    }

    @Override
    public ServerPlayer getEntity() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public ResourceKey<Level> getFromDimension() {
        return fromDimension;
    }

    public ResourceKey<Level> getToDimension() {
        return toDimension;
    }
}