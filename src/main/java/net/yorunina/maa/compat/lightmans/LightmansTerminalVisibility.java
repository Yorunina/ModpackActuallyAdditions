package net.yorunina.maa.compat.lightmans;

import dev.latvian.mods.kubejs.script.ScriptType;
import io.github.lightman314.lightmanscurrency.LCText;
import io.github.lightman314.lightmanscurrency.api.traders.ITraderSource;
import io.github.lightman314.lightmanscurrency.api.traders.TraderAPI;
import io.github.lightman314.lightmanscurrency.api.traders.TraderData;
import io.github.lightman314.lightmanscurrency.common.menus.validation.MenuValidator;
import io.github.lightman314.lightmanscurrency.common.menus.validation.types.BlockValidator;
import io.github.lightman314.lightmanscurrency.common.menus.validation.types.ItemValidator;
import io.github.lightman314.lightmanscurrency.common.menus.validation.types.SimpleValidator;
import io.github.lightman314.lightmanscurrency.common.player.LCAdminMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.compat.kubejs.MAAEvents;
import net.yorunina.maa.compat.kubejs.events.LightmansTerminalVisibilityEventJS;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class LightmansTerminalVisibility {
    private LightmansTerminalVisibility() {
    }

    public enum Source {
        PORTABLE("portable"),
        BLOCK("block"),
        COMMAND("command"),
        UNKNOWN("unknown");

        public final String id;

        Source(String id) {
            this.id = id;
        }
    }

    public static Source sourceOf(@Nullable MenuValidator validator) {
        if (validator instanceof ItemValidator) {
            return Source.PORTABLE;
        }
        if (validator instanceof BlockValidator) {
            return Source.BLOCK;
        }
        if (validator instanceof SimpleValidator) {
            return Source.COMMAND;
        }
        return Source.UNKNOWN;
    }

    public static boolean canSee(@Nullable Player player, @Nullable TraderData trader, Source source) {
        if (trader == null) {
            return false;
        }
        if (player == null) {
            return true;
        }
        if (LCAdminMode.isAdminPlayer(player)) {
            return true;
        }
        LightmansTerminalVisibilityEventJS event = new LightmansTerminalVisibilityEventJS(player, trader, source);
        ScriptType type = player.level().isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
        MAAEvents.LIGHTMANS_TERMINAL_VISIBILITY.post(type, event);
        return event.isVisible();
    }

    public static List<TraderData> filter(List<TraderData> traders, @Nullable Player player, Source source) {
        if (traders == null || traders.isEmpty()) {
            return traders == null ? List.of() : traders;
        }
        List<TraderData> result = new ArrayList<>(traders.size());
        for (TraderData trader : traders) {
            if (canSee(player, trader, source)) {
                result.add(trader);
            }
        }
        return result;
    }

    public static Supplier<ITraderSource> networkSource(Player player, MenuValidator validator, boolean isClient) {
        Source source = sourceOf(validator);
        return () -> new FilteredNetworkTraderSource(player, source, isClient);
    }

    private static final class FilteredNetworkTraderSource implements ITraderSource {
        private final Player player;
        private final Source source;
        private final boolean isClient;

        private FilteredNetworkTraderSource(Player player, Source source, boolean isClient) {
            this.player = player;
            this.source = source;
            this.isClient = isClient;
        }

        @Override
        public List<TraderData> getTraders() {
            return filter(TraderAPI.getApi().GetAllNetworkTraders(isClient), player, source);
        }

        @Override
        public boolean isSingleTrader() {
            return false;
        }

        @Override
        public boolean showSearchBox() {
            return true;
        }

        @Override
        @Nullable
        public Component getCustomTitle() {
            return LCText.GUI_TRADER_ALL_NETWORK_TRADERS.get();
        }
    }
}