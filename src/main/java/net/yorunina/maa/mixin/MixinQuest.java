package net.yorunina.maa.mixin;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import questsadditions.QuestsAdditions;
import questsadditions.mixinutils.IMixinQuest;

import java.util.List;

@Mixin(Quest.class)
public class MixinQuest{
    @Inject(
            method = {"isVisible"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    public void isVisible(TeamData data, CallbackInfoReturnable<Boolean> cir) {
        List<QuestObject> incompatible = ((IMixinQuest)this).getIncompatibleQuests();
        if (!incompatible.isEmpty()) {
            for(QuestObject pQuest : incompatible) {
                if (pQuest.isValid() && data.isCompleted(pQuest)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
