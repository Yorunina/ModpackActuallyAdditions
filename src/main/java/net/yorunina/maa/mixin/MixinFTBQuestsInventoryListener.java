package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.util.FTBQuestsInventoryListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(FTBQuestsInventoryListener.class)
public abstract class MixinFTBQuestsInventoryListener {
    @Shadow
    @Final
    public ServerPlayer player;
    @Shadow
    private static final Map<Item, List<ItemStack>> inventorySummaryCache = new HashMap<>();
    @Shadow
    private static final List<ItemStack> nonEmptyStacks = new ArrayList<>();

    @Shadow
    private static void buildInventorySummary(ServerPlayer player) {
    }

    @Inject(method = "detect", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.AFTER),  cancellable = true, remap = false)
    private static void detectInject(ServerPlayer player, ItemStack craftedItem, long sourceTask, CallbackInfo ci, @Local(name = "file") ServerQuestFile file, @Local(name = "tasksToCheck") List<Task> tasksToCheck) {
        TeamData data = file.getOrCreateTeamData(player);
        if (data != null && !data.isLocked()) {
            file.withPlayerContext(player, () -> {
                buildInventorySummary(player);
                for (Task task : tasksToCheck) {
                    if (task.id != sourceTask && data.canStartTasks(task.getQuest())) {
                        task.submitTask(data, player, craftedItem);
                    }
                }
                inventorySummaryCache.clear();
                nonEmptyStacks.clear();
            });
        }
        ci.cancel();
    }
}
