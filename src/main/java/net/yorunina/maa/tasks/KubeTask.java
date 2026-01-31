package net.yorunina.maa.tasks;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KubeTask extends Task {
    private String kubeId;
    private long count;

    public KubeTask(long id, Quest quest) {
        super(id, quest);
        this.kubeId = "";
        this.count = 1L;
    }

    @Override
    public TaskType getType() {
        return TasksRegistry.KUBE;
    }

    @Override
    public long getMaxProgress() {
        return this.count;
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        nbt.putString("kubeId", this.kubeId);
        nbt.putLong("count", this.count);
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        this.kubeId = nbt.getString("kubeId");
        this.count = Math.max(nbt.getLong("count"), 1L);
    }

    @Override
    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeUtf(this.kubeId, 32767);
        buffer.writeVarLong(this.count);
    }

    @Override
    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.kubeId = buffer.readUtf(32767);
        this.count = buffer.readVarLong();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addString("kubeId", this.kubeId, (v) -> this.kubeId = v, "");
        config.addLong("count", this.count, (v) -> this.count = v, 1L, 1L, Long.MAX_VALUE);
    }

    public String getKubeId() {
        return this.kubeId;
    }
}
