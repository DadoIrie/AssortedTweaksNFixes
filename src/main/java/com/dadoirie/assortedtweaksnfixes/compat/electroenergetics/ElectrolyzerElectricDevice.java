package com.dadoirie.assortedtweaksnfixes.compat.electroenergetics;

import com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer.BasinOperatingBlockEntityAccessor;
import com.george_vi.electroenergetics.devices.device.DevicesSavedData;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.SimpleElectricalDevice;
import com.george_vi.electroenergetics.simulation.BridgeCollector;
import com.george_vi.electroenergetics.simulation.SimulationResults;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectrolyzerElectricDevice extends SimpleElectricalDevice {

    public interface Electric {
        double atnf$kilowatts();
        double atnf$voltage();
        void atnf$setElectric(double kilowatts, double voltage);
    }

    public interface Machine {
        void atnf$setNodeVoltage(double voltage);
    }

    public static final double RUN_MIN_FACTOR = 0.95;
    public static final double WASTE_FACTOR = 1.05;
    public static final double ENERGIZED_MIN_VOLTAGE = 1.0;

    private static final double MIN_MARGIN_VOLTS = 20.0;

    private static final double OPEN_CIRCUIT_RESISTANCE = 999_999.0;
    private static final double SENSE_RESISTANCE = 100_000.0;

    public static int minVoltage(double ratedVoltage) {
        double margin = Math.max(ratedVoltage * (1.0 - RUN_MIN_FACTOR), MIN_MARGIN_VOLTS);
        double raw = ratedVoltage - margin;
        double rounded = Math.floor(raw / 10.0) * 10.0;
        return (int) Math.max(rounded, ENERGIZED_MIN_VOLTAGE);
    }

    public static int maxVoltage(double ratedVoltage) {
        double margin = Math.max(ratedVoltage * (WASTE_FACTOR - 1.0), MIN_MARGIN_VOLTS);
        double raw = ratedVoltage + margin;
        return (int) (Math.ceil(raw / 10.0) * 10.0);
    }

    public ElectrolyzerElectricDevice(Level level, BlockPos pos, DevicesSavedData deviceSD, SimulatedDeviceType<?> type) {
        super(level, pos, deviceSD, type);
    }

    @Override
    public void preTick(BridgeCollector bridges) {
        bridges.builder(pos).resistor(0, 1, currentResistance());
    }

    private double currentResistance() {
        if (!level.isLoaded(pos))
            return OPEN_CIRCUIT_RESISTANCE;
        if (!(level.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity electrolyzer))
            return OPEN_CIRCUIT_RESISTANCE;
        if (electrolyzer.speed == null)
            return OPEN_CIRCUIT_RESISTANCE;

        int dial = electrolyzer.speed.getValue();
        if (dial <= 0)
            return OPEN_CIRCUIT_RESISTANCE;

        if (!(((BasinOperatingBlockEntityAccessor) electrolyzer).atnf$getCurrentRecipe()
                instanceof ElectrolyzerElectricDevice.Electric electric))
            return SENSE_RESISTANCE;

        double targetWatts = electric.atnf$kilowatts() * 1000.0 * (dial / 50.0);
        double rated = electric.atnf$voltage();
        return (rated * rated) / targetWatts;
    }

    @Override
    public void postTick(SimulationResults results) {
        if (!level.isLoaded(pos))
            return;
        if (!(level.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity electrolyzer))
            return;

        ((ElectrolyzerElectricDevice.Machine) electrolyzer)
                .atnf$setNodeVoltage(Math.abs(results.getVoltageAt(pos, 0, 1)));
    }

    @Override
    public boolean shouldRemove(BlockState oldState, BlockState newState) {
        return oldState.getBlock().getClass() != newState.getBlock().getClass();
    }
}