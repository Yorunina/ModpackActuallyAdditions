package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.yorunina.maa.compat.kubejs.events.FTBQuestCheckRepeatableJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import questsadditions.mixinutils.IMixinQuest;

import java.util.List;
import java.util.UUID;

import static net.yorunina.maa.compat.kubejs.MAAEvents.FTB_QUEST_CHECK_REPEATABLE;

@Mixin(value = Quest.class, remap = false)
public abstract class MixinQuest {
    @Shadow
    public abstract long getMovableID();

    @Inject(
            method = {"isVisible"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void isVisible(TeamData data, CallbackInfoReturnable<Boolean> cir) {
        List<QuestObject> incompatible = ((IMixinQuest) this).getIncompatibleQuests();
        if (!incompatible.isEmpty()) {
            for (QuestObject pQuest : incompatible) {
                if (pQuest.isValid() && data.isCompleted(pQuest)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(method = "checkRepeatable",
            at = {@At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;forceProgress(Ldev/ftb/mods/ftbquests/quest/TeamData;Ldev/ftb/mods/ftbquests/util/ProgressChange;)V")},
            cancellable = true)
    public void checkRepeatable(TeamData data, UUID player, CallbackInfoReturnable<Boolean> cir) {
        String codexID = String.format("%016X", this.getMovableID());
        FTBQuestCheckRepeatableJS event = new FTBQuestCheckRepeatableJS(data, player, codexID);
        if (FTB_QUEST_CHECK_REPEATABLE.post(event, codexID).arch().isFalse()) {
            cir.setReturnValue(false);
        }
    }
}
