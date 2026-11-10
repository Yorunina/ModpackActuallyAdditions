package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class ItemCooldownStartedEventJS extends PlayerEventJS {
    private final ServerPlayer player;
    private final Item item;
    private final int duration;

    public ItemCooldownStartedEventJS(ServerPlayer player, Item item, int duration) {
        this.player = player;
        this.item = item;
        this.duration = duration;
    }

    @Override
    public ServerPlayer getEntity() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public int getDuration() {
        return duration;
    }
}
