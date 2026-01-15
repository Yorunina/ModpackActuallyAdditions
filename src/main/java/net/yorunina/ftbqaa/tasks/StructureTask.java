package net.yorunina.ftbqaa.tasks;


import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.net.FTBQuestsNetHandler;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import dev.ftb.mods.ftblibrary.ui.Button;

import java.awt.*;

public class StructureTask extends Task {
    public String name = "";
    public boolean hasCustomPicture;
    public boolean layer = true;
    public ItemStack item;
    public boolean rightclick_validation;
    public boolean ignoreState;

    public StructureTask(long id, Quest quest) {
        super(id, quest);
        this.item = ItemStack.f_41583_;
    }

    public TaskType getType() {
        return TasksRegistry.STRUCTURE;
    }

    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putString("name", this.name);
        nbt.putBoolean("hasCustomPicture", this.hasCustomPicture);
        nbt.putBoolean("layer", this.layer);
        nbt.putBoolean("ignoreState", this.ignoreState);
        nbt.putBoolean("rightclick", this.rightclick_validation);
        if (this.rightclick_validation) {
            NBTUtils.write(nbt, "item", this.item);
        }

    }

    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        this.name = nbt.getString("name");
        this.hasCustomPicture = nbt.getBoolean("hasCustomPicture");
        this.layer = nbt.getBoolean("layer");
        this.ignoreState = nbt.getBoolean("ignoreState");
        this.rightclick_validation = nbt.getBoolean("rightclick");
        if (this.rightclick_validation) {
            this.item = NBTUtils.read(nbt, "item");
        }

    }

    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(this.name, 32767);
        buffer.writeBoolean(this.hasCustomPicture);
        buffer.writeBoolean(this.layer);
        buffer.writeBoolean(this.ignoreState);
        buffer.writeBoolean(this.rightclick_validation);
        if (this.rightclick_validation) {
            FTBQuestsNetHandler.writeItemType(buffer, this.item);
        }

    }

    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.name = buffer.readUtf(32767);
        this.hasCustomPicture = buffer.readBoolean();
        this.layer = buffer.readBoolean();
        this.ignoreState = buffer.readBoolean();
        this.rightclick_validation = buffer.readBoolean();
        if (this.rightclick_validation) {
            this.item = FTBQuestsNetHandler.readItemType(buffer);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addString("name", this.name, (v) -> this.name = v, "");
        config.addBool("hasCustomPicture", this.hasCustomPicture, (v) -> this.hasCustomPicture = v, false);
        config.addBool("layer", this.layer, (v) -> this.layer = v, true);
        config.addBool("ignoreState", this.ignoreState, (v) -> this.ignoreState = v, false);
        config.addBool("rightclick", this.rightclick_validation, (v) -> this.rightclick_validation = v, false);
        config.addItemStack("item", this.item, (v) -> this.item = v, ItemStack.EMPTY, true, true).setNameKey("ftbquests.task.ftbquests.item");
    }

    @OnlyIn(Dist.CLIENT)
    public Component getAltTitle() {
        return Component.translatable("questsadditions.task.structure.title", new Object[]{this.name});
    }

    @OnlyIn(Dist.CLIENT)
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        list.blankLine();
        if (StructurePlacementClient.isLocked()) {
            list.add(Component.translatable("ftbquests.task.click_to_submit").m_130944_(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.UNDERLINE}));
        } else {
            list.add(Component.translatable("questsadditions.structure_task.show").m_130944_(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.UNDERLINE}));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        return this.hasCustomPicture ? Icon.getIcon("questsadditions:textures/structure_icons/" + this.name + ".png") : super.getAltIcon();
    }

    @OnlyIn(Dist.CLIENT)
    public void onButtonClicked(Button button, boolean canClick) {
        button.playClickSound();
        if (!ClientQuestFile.INSTANCE.selfTeamData.isCompleted(this)) {
            StructurePlacementClient.toggleShowStructure(this.name, this.layer, this);
        }

    }
}

