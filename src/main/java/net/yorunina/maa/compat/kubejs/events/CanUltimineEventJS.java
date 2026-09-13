package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CanUltimineEventJS extends PlayerEventJS {
    private final Player player;

    public CanUltimineEventJS(Player player) {
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public ItemStack getMainHandItem() {
        return player.getMainHandItem();
    }
}