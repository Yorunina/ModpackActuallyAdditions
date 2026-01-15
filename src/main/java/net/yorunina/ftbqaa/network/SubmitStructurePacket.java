package net.yorunina.ftbqaa.network;


import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.yorunina.ftbqaa.tasks.StructureTask;
import net.yorunina.ftbqaa.tasks.TasksRegistry;
import net.yorunina.ftbqaa.utils.StructureLoader;

public class SubmitStructurePacket extends BaseC2SMessage {
    private final long taskId;
    private final BlockPos base;
    private final Rotation rot;

    public SubmitStructurePacket(long taskId, BlockPos base, Rotation rot) {
        this.taskId = taskId;
        this.base = base;
        this.rot = rot;
    }

    public SubmitStructurePacket(FriendlyByteBuf buffer) {
        this.taskId = buffer.readLong();
        this.base = buffer.readBlockPos();
        this.rot = Rotation.values()[buffer.readByte()];
    }

    public MessageType getType() {
        return PacketHandler.SUBMIT_STRUCTURE;
    }

    public void handle(NetworkManager.PacketContext context) {
        List<StructureTask> tasks = TasksRegistry.getInstance().getStructureTasks();
        if (!tasks.isEmpty()) {
            for(StructureTask task : tasks) {
                if (task.id == this.taskId) {
                    TeamData data = ServerQuestFile.INSTANCE.getOrCreateTeamData(context.getPlayer());
                    if (!data.isCompleted(task)) {
                        List<StructureTemplate.StructureBlockInfo> blocks = StructureLoader.get(task.name);
                        if (StructureLoader.isValidStructure(context.getPlayer().level(), this.base, this.rot, blocks, task.ignoreState)) {
                            data.addProgress(task, 1L);
                        }
                    }
                }
            }

        }
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(this.taskId);
        buffer.writeBlockPos(this.base);
        buffer.writeByte(this.rot.ordinal());
    }
}
