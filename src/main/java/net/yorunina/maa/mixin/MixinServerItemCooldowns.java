package net.yorunina.maa.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ServerItemCooldowns;
import net.yorunina.maa.compat.kubejs.MAAEvents;
import net.yorunina.maa.compat.kubejs.events.ItemCooldownStartedEventJS;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerItemCooldowns.class)
public abstract class MixinServerItemCooldowns extends ItemCooldowns {
    @Shadow @Final private ServerPlayer player;

    @Override
    public void addCooldown(Item item, int duration) {
        boolean alreadyCoolingDown = isOnCooldown(item);
        super.addCooldown(item, duration);
        if (!alreadyCoolingDown && duration > 0 && MAAEvents.ITEM_COOLDOWN_STARTED.hasListeners()) {
            MAAEvents.ITEM_COOLDOWN_STARTED.post(new ItemCooldownStartedEventJS(player, item, duration));
        }
    }
}
