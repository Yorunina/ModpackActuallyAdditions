package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.unusual.block_factorys_bosses.capability.entity.RollCap;


public class RollEventJS extends PlayerEventJS {
    private final Player player;
    private final float leftImpulse;
    private final float forwardImpulse;
    private final RollCap.RollCapHandler.RollType direction;

    public RollEventJS(Player player, float leftImpulse, float forwardImpulse, RollCap.RollCapHandler.RollType direction) {
        this.player = player;
        this.leftImpulse = leftImpulse;
        this.forwardImpulse = forwardImpulse;
        this.direction = direction;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public Player getPlayer() {
        return player;
    }

    public float getLeftImpulse() {
        return leftImpulse;
    }

    public float getForwardImpulse() {
        return forwardImpulse;
    }

    public RollCap.RollCapHandler.RollType getDirection() {
        return direction;
    }

    public String getDirectionName() {
        return direction.name();
    }
}
