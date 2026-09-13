package net.yorunina.maa.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.integration.FTBUltiminePlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.entity.player.Player;
import net.yorunina.maa.compat.kubejs.MAAEvents;
import net.yorunina.maa.compat.kubejs.events.CanUltimineEventJS;

public final class FTBUltimineCompat {
    private FTBUltimineCompat() {
    }

    public static void init() {
        FTBUltiminePlugin.register(new KubeJSUltimineRestriction());
    }

    private static final class KubeJSUltimineRestriction implements FTBUltiminePlugin {
        @Override
        public boolean canUltimine(Player player) {
            if (player == null) {
                return false;
            }
            ScriptType type = player.level().isClientSide() ? ScriptType.CLIENT : ScriptType.SERVER;
            return MAAEvents.CAN_ULTIMINE.post(type, new CanUltimineEventJS(player)).arch().isTrue();
        }
    }
}