package net.yorunina.maa.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import io.github.lightman314.lightmanscurrency.api.traders.TraderData;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.compat.lightmans.LightmansTerminalVisibility;

public class LightmansTerminalVisibilityEventJS extends PlayerEventJS {
    private final Player player;
    private final TraderData trader;
    private final LightmansTerminalVisibility.Source source;
    private boolean visible = true;

    public LightmansTerminalVisibilityEventJS(Player player, TraderData trader, LightmansTerminalVisibility.Source source) {
        this.player = player;
        this.trader = trader;
        this.source = source;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public TraderData getTrader() {
        return trader;
    }

    public long getTraderId() {
        return trader.getID();
    }

    public String getPersistentId() {
        return trader.getPersistentID();
    }

    public boolean isPersistent() {
        return trader.isPersistent();
    }

    public String getSource() {
        return source.id;
    }

    public boolean isPortable() {
        return source == LightmansTerminalVisibility.Source.PORTABLE;
    }

    public boolean isBlock() {
        return source == LightmansTerminalVisibility.Source.BLOCK;
    }

    public boolean isCommand() {
        return source == LightmansTerminalVisibility.Source.COMMAND;
    }

    public boolean isVisible() {
        return visible;
    }

    public void hide() {
        this.visible = false;
    }

    public void show() {
        this.visible = true;
    }
}