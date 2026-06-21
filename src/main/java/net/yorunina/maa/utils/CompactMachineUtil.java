package net.yorunina.maa.utils;

import dev.compactmods.machines.api.dimension.CompactDimension;
import dev.compactmods.machines.api.tunnels.capability.CapabilityTunnel;
import dev.compactmods.machines.machine.CompactMachineItem;
import dev.compactmods.machines.tunnel.TunnelWallEntity;
import dev.compactmods.machines.tunnel.Tunnels;
import dev.compactmods.machines.tunnel.definitions.FluidTunnel;
import dev.compactmods.machines.tunnel.definitions.ItemTunnel;
import dev.compactmods.machines.tunnel.graph.TunnelConnectionGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.yorunina.maa.mixin.accessor.FluidTunnelInstanceAccessor;
import net.yorunina.maa.mixin.accessor.ItemTunnelInstanceAccessor;

import java.util.*;
import java.util.function.Consumer;

public class CompactMachineUtil {
    public static Optional<ChunkPos> getRoomFromItem(ItemStack stack) {
        return CompactMachineItem.getRoom(stack);
    }

    public static Optional<ServerLevel> getCompactDimension(MinecraftServer server) {
        return Optional.ofNullable(server.getLevel(CompactDimension.LEVEL_KEY));
    }

    public static Optional<TunnelConnectionGraph> getTunnelGraph(MinecraftServer server, ChunkPos room) {
        return getCompactDimension(server).map(compactDim -> TunnelConnectionGraph.forRoom(compactDim, room));
    }

    public static Optional<ItemTunnel.Instance> getItemTunnelInstance(MinecraftServer server, ChunkPos room, Direction side) {
        return findTunnelEntityByRoom(server, room, side, ForgeCapabilities.ITEM_HANDLER)
                .map(TunnelWallEntity::getTunnel);
    }

    public static Optional<FluidTunnel.Instance> getFluidTunnelInstance(MinecraftServer server, ChunkPos room, Direction side) {
        return findTunnelEntityByRoom(server, room, side, ForgeCapabilities.FLUID_HANDLER)
                .map(TunnelWallEntity::getTunnel);
    }

    public static LazyOptional<IItemHandler> getItemHandler(MinecraftServer server, ChunkPos room, Direction side) {
        return findTunnelEntityByRoom(server, room, side, ForgeCapabilities.ITEM_HANDLER)
                .map(twe -> twe.getTunnelCapability(ForgeCapabilities.ITEM_HANDLER, side))
                .orElse(LazyOptional.empty());
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(MinecraftServer server, ChunkPos room, Direction side) {
        return findTunnelEntityByRoom(server, room, side, ForgeCapabilities.FLUID_HANDLER)
                .map(twe -> twe.getTunnelCapability(ForgeCapabilities.FLUID_HANDLER, side))
                .orElse(LazyOptional.empty());
    }

    public static List<ItemStack> getAllItems(MinecraftServer server, ChunkPos room, Direction side) {
        List<ItemStack> result = new ArrayList<>();
        getItemTunnelInstance(server, room, side).ifPresent(inst -> {
            ItemStackHandler handler = ((ItemTunnelInstanceAccessor)inst).getHandler();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    result.add(stack.copy());
                }
            }
        });
        return result;
    }

    public static Optional<FluidStack> getFluid(MinecraftServer server, ChunkPos room, Direction side) {
        return getFluidTunnelInstance(server, room, side)
                .map(inst -> ((FluidTunnelInstanceAccessor)inst).getHandler().getFluid());
    }

    public static int getFluidAmount(MinecraftServer server, ChunkPos room, Direction side) {
        return getFluidTunnelInstance(server, room, side)
                .map(inst -> ((FluidTunnelInstanceAccessor)inst).getHandler().getFluidAmount())
                .orElse(0);
    }

    public static int getFluidCapacity(MinecraftServer server, ChunkPos room, Direction side) {
        return getFluidTunnelInstance(server, room, side)
                .map(inst -> ((FluidTunnelInstanceAccessor)inst).getHandler().getCapacity())
                .orElse(0);
    }

    public static ItemStack insertItem(MinecraftServer server, ChunkPos room, Direction side, ItemStack stack, boolean simulate) {
        LazyOptional<IItemHandler> handler = getItemHandler(server, room, side);
        if (handler.isPresent()) {
            Optional<IItemHandler> resolved = handler.resolve();
            if (resolved.isPresent()) {
                return resolved.get().insertItem(0, stack, simulate);
            }
        }
        return stack;
    }

    public static ItemStack extractItem(MinecraftServer server, ChunkPos room, Direction side, int slot, int amount, boolean simulate) {
        LazyOptional<IItemHandler> handler = getItemHandler(server, room, side);
        if (handler.isPresent()) {
            Optional<IItemHandler> resolved = handler.resolve();
            if (resolved.isPresent()) {
                return resolved.get().extractItem(slot, amount, simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    public static int fillFluid(MinecraftServer server, ChunkPos room, Direction side, FluidStack resource, IFluidHandler.FluidAction action) {
        LazyOptional<IFluidHandler> handler = getFluidHandler(server, room, side);
        if (handler.isPresent()) {
            Optional<IFluidHandler> resolved = handler.resolve();
            if (resolved.isPresent()) {
                return resolved.get().fill(resource, action);
            }
        }
        return 0;
    }

    public static FluidStack drainFluid(MinecraftServer server, ChunkPos room, Direction side, int maxDrain, IFluidHandler.FluidAction action) {
        LazyOptional<IFluidHandler> handler = getFluidHandler(server, room, side);
        if (handler.isPresent()) {
            Optional<IFluidHandler> resolved = handler.resolve();
            if (resolved.isPresent()) {
                return resolved.get().drain(maxDrain, action);
            }
        }
        return FluidStack.EMPTY;
    }

    public static FluidStack drainFluid(MinecraftServer server, ChunkPos room, Direction side, FluidStack resource, IFluidHandler.FluidAction action) {
        LazyOptional<IFluidHandler> handler = getFluidHandler(server, room, side);
        if (handler.isPresent()) {
            Optional<IFluidHandler> resolved = handler.resolve();
            if (resolved.isPresent()) {
                return resolved.get().drain(resource, action);
            }
        }
        return FluidStack.EMPTY;
    }

    public static Map<Direction, List<ItemStack>> getAllItemsBySide(MinecraftServer server, ChunkPos room) {
        Map<Direction, List<ItemStack>> result = new EnumMap<>(Direction.class);
        for (Direction side : Direction.values()) {
            List<ItemStack> items = getAllItems(server, room, side);
            if (!items.isEmpty()) {
                result.put(side, items);
            }
        }
        return result;
    }

    public static Map<Direction, FluidStack> getAllFluidsBySide(MinecraftServer server, ChunkPos room) {
        Map<Direction, FluidStack> result = new EnumMap<>(Direction.class);
        for (Direction side : Direction.values()) {
            getFluid(server, room, side).ifPresent(fluid -> {
                if (!fluid.isEmpty()) {
                    result.put(side, fluid);
                }
            });
        }
        return result;
    }

    public static void forEachItemTunnel(MinecraftServer server, ChunkPos room, Consumer<ItemTunnelInfo> consumer) {
        var compactDimOpt = getCompactDimension(server);
        if (compactDimOpt.isEmpty()) return;
        final var compactDim = compactDimOpt.get();

        final var graph = TunnelConnectionGraph.forRoom(compactDim, room);
        graph.tunnels().forEach(info -> {
            if (compactDim.getBlockEntity(info.location()) instanceof TunnelWallEntity twe) {
                ItemTunnel.Instance inst = twe.getTunnel();
                if (inst != null) {
                    consumer.accept(new ItemTunnelInfo(
                            info.side(), info.location(), ((ItemTunnelInstanceAccessor)inst).getHandler()
                    ));
                }
            }
        });
    }

    public static void forEachFluidTunnel(MinecraftServer server, ChunkPos room, Consumer<FluidTunnelInfo> consumer) {
        var compactDimOpt = getCompactDimension(server);
        if (compactDimOpt.isEmpty()) return;
        final var compactDim = compactDimOpt.get();

        final var graph = TunnelConnectionGraph.forRoom(compactDim, room);
        graph.tunnels().forEach(info -> {
            if (compactDim.getBlockEntity(info.location()) instanceof TunnelWallEntity twe) {
                FluidTunnel.Instance inst = twe.getTunnel();
                if (inst != null) {
                    consumer.accept(new FluidTunnelInfo(
                            info.side(), info.location(), ((FluidTunnelInstanceAccessor)inst).getHandler()
                    ));
                }
            }
        });
    }


    private static Optional<TunnelWallEntity> findTunnelEntityByRoom(MinecraftServer server, ChunkPos room, Direction side, Capability<?> cap) {
        final var compactDim = server.getLevel(CompactDimension.LEVEL_KEY);
        if (compactDim == null) return Optional.empty();

        final var graph = TunnelConnectionGraph.forRoom(compactDim, room);

        Optional<TunnelWallEntity> result = Optional.empty();
        for (var info : graph.tunnels().toList()) {
            if (info.side() != side) continue;

            var def = Tunnels.getDefinition(info.type());
            if (!(def instanceof CapabilityTunnel<?> ct)) continue;
            if (!ct.getSupportedCapabilities().contains(cap)) continue;

            if (compactDim.getBlockEntity(info.location()) instanceof TunnelWallEntity twe) {
                result = Optional.of(twe);
                break;
            }
        }
        return result;
    }

    public static final class ItemTunnelInfo {
        private final Direction connectedSide;
        private final BlockPos tunnelPosition;
        private final ItemStackHandler handler;

        ItemTunnelInfo(Direction connectedSide, BlockPos tunnelPosition, ItemStackHandler handler) {
            this.connectedSide = connectedSide;
            this.tunnelPosition = tunnelPosition;
            this.handler = handler;
        }

        public Direction connectedSide() { return connectedSide; }
        public BlockPos tunnelPosition() { return tunnelPosition; }
        public ItemStackHandler handler() { return handler; }

        public int slotCount() { return handler.getSlots(); }

        public List<ItemStack> getAllItems() {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) items.add(stack.copy());
            }
            return items;
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return handler.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return handler.extractItem(slot, amount, simulate);
        }
    }

    public record FluidTunnelInfo(Direction connectedSide, BlockPos tunnelPosition, FluidTank handler) {

        public FluidStack getFluid() {
            return handler.getFluid();
        }

        public int getFluidAmount() {
            return handler.getFluidAmount();
        }

        public int getCapacity() {
            return handler.getCapacity();
        }

            public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
                return handler.fill(resource, action);
            }

            public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
                return handler.drain(maxDrain, action);
            }

            public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
                return handler.drain(resource, action);
            }
        }

}
