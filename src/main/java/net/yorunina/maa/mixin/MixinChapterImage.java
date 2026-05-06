package net.yorunina.maa.mixin;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.util.ConfigQuestObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Predicate;

@Mixin(ChapterImage.class)
public abstract class MixinChapterImage {
    @Unique
    private Quest hideDependency = null;

    @Shadow
    private Chapter chapter;

    @Inject(method = "writeData", at = @At(value = "HEAD"), remap = false)
    private void writeData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (hideDependency != null) nbt.putString("hideDependency", hideDependency.getCodeString());
    }

    @Inject(method = "readData", at = @At(value = "HEAD"), remap = false)
    private void readData(CompoundTag nbt, CallbackInfo ci) {
        hideDependency = nbt.contains("hideDependency") ? chapter.file.getQuest(chapter.file.getID(nbt.get("hideDependency"))) : null;
    }


    @Inject(method = "writeNetData", at = @At(value = "TAIL"), remap = false)
    private void writeNetData(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeLong(hideDependency == null ? 0L : hideDependency.id);
    }

    @Inject(method = "readNetData", at = @At(value = "TAIL"), remap = false)
    private void readNetData(FriendlyByteBuf buffer, CallbackInfo ci) {
        hideDependency = chapter.file.getQuest(buffer.readLong());
    }

    @Inject(method = "fillConfigGroup", at = @At(value = "TAIL"), remap = false)
    private void fillConfigGroup(ConfigGroup config, CallbackInfo ci) {
        Predicate<QuestObjectBase> depTypes = object -> object == null || object instanceof Quest;
        config.add("hideDependency", new ConfigQuestObject<>(depTypes), hideDependency, v -> hideDependency = v, null).setNameKey("ftbquests.chapter_image.hide_dependency");
    }

    @Inject(method = "shouldShowImage", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void shouldShowImage(TeamData teamData, CallbackInfoReturnable<Boolean> cir) {
        if (hideDependency != null && teamData.isCompleted(hideDependency)) cir.setReturnValue(false);
    }
}
