package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public class VillagerUpdateSpecialPrices extends EventJS {
    private final Villager villager;
    private final Player player;

    public VillagerUpdateSpecialPrices(Player player, Villager villager) {
        this.villager = villager;
        this.player =  player;
    }

    public Player getPlayer() {
        return player;
    }

    public Villager getVillager() {
        return villager;
    }
}
