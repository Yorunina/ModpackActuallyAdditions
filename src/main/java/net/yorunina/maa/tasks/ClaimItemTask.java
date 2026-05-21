package net.yorunina.maa.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.IconAnimation;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import dev.ftb.mods.ftbquests.net.FTBQuestsNetHandler;
import dev.ftb.mods.ftbquests.net.SubmitTaskMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class ClaimItemTask extends Task {
    private ItemStack itemStack;

    public ClaimItemTask(long id, Quest quest) {
        super(id, quest);
        this.itemStack = ItemStack.EMPTY;
    }

    @Override
    public TaskType getType() {
        return TasksRegistry.CLAIM_ITEM;
    }

    @Override
    public long getMaxProgress() {
        return 1;
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        NBTUtils.write(nbt, "itemStack", this.itemStack);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        this.itemStack = NBTUtils.read(nbt, "itemStack");
    }

    @Override
    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        FTBQuestsNetHandler.writeItemType(buffer, this.itemStack);
    }

    @Override
    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.itemStack = FTBQuestsNetHandler.readItemType(buffer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addItemStack("item", this.itemStack, (v) -> this.itemStack = v, ItemStack.EMPTY, true, false).setNameKey("ftbquests.task.ftbquests.item");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public MutableComponent getAltTitle() {
        return Component.translatable("ftbquests.task.maa.claim_item.alt_title", this.itemStack.getCount(), this.itemStack.getHoverName());
    }


    public List<ItemStack> getValidDisplayItems() {
        List<ItemStack> list = new ArrayList<>();
        ItemFiltersAPI.getDisplayItemStacks(this.itemStack, list);
        return list;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        List<Icon> icons = new ArrayList<>();

        for (ItemStack stack : this.getValidDisplayItems()) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            Icon icon = ItemIcon.getItemIcon(copy);
            if (!icon.isEmpty()) {
                icons.add(icon);
            }
        }

        if (icons.isEmpty()) {
            return ItemIcon.getItemIcon(FTBQuestsItems.MISSING_ITEM.get());
        } else {
            return IconAnimation.fromList(icons, false);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
        button.playClickSound();
        (new SubmitTaskMessage(this.id)).sendToServer();
    }

    @Override
    public int autoSubmitOnPlayerTick() {
        return 0;
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (this.checkTaskSequence(teamData) && !teamData.isCompleted(this)) {
            player.addItem(this.itemStack.copy());
            teamData.addProgress(this, 1L);
        }
    }
}
