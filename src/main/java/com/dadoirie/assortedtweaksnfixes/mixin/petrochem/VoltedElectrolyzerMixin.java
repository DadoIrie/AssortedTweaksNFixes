package com.dadoirie.assortedtweaksnfixes.mixin.petrochem;

import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ATNFSimulatedDevices;
import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ElectrolyzerElectricDevice;
import com.george_vi.electroenergetics.foundation.CEELang;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.ElectricalDeviceBlock;
import com.george_vi.electroenergetics.foundation.nodes.NodeConfigurator;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(ElectrolyzerBlock.class)
public abstract class VoltedElectrolyzerMixin implements ElectricalDeviceBlock<ElectrolyzerElectricDevice> {

    @Unique
    private static final NodeConfigurator ATNF$NODES = new NodeConfigurator.Builder()
            .add(16, 8, 8)
            .add(0, 8, 8)
            .simple(Direction.SOUTH);

    @Override
    public SimulatedDeviceType<ElectrolyzerElectricDevice> getDevice() {
        return ATNFSimulatedDevices.ELECTROLYZER.get();
    }

    @Override
    public Map<Integer, Vec3> getNodePositions(Level level, BlockPos pos, BlockState state) {
        return ATNF$NODES.getNodes(state.getValue(ElectrolyzerBlock.HORIZONTAL_FACING));
    }

    @Override
    public Vec3 getNodePosition(Level level, BlockPos pos, BlockState state, int id) {
        return ATNF$NODES.getNodePos(state.getValue(ElectrolyzerBlock.HORIZONTAL_FACING), id);
    }

    @Override
    public MutableComponent getNodeLabel(Level level, BlockPos pos, BlockState state, int id) {
        return CEELang.nodeLabel("node");
    }
}