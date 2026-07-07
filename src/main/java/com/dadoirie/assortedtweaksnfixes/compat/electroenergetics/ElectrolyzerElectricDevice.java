package com.dadoirie.assortedtweaksnfixes.compat.electroenergetics;

import com.george_vi.electroenergetics.config.CEEConfigs;
import com.george_vi.electroenergetics.devices.device.DevicesSavedData;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.SimpleElectricalDevice;
import com.george_vi.electroenergetics.simulation.BridgeCollector;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * CEE circuit participant for Petrochem's electrolyzer.
 * <p>
 * Stateless by design: every tick the load is derived directly from the block
 * entity's speed dial. Nothing is cached, persisted, or synced from here.
 * When display/consumption is needed later, computed values get pushed into
 * the block entity (the sync channel), never stored on this device.
 */
public class ElectrolyzerElectricDevice extends SimpleElectricalDevice {

    /** Grid voltage the machine is designed for. Placeholder until config. */
    public static final double RATED_VOLTAGE = 220.0;

    /**
     * Power demand at the 50% base dial, expressed in FE/t purely as the
     * unit of account (converted to watts via CEE's own conversion rate).
     * Placeholder until the recipe hookup step, where this comes from the
     * active recipe's energy value instead.
     */
    public static final double BASE_FE_PER_TICK = 100.0;

    /** Effectively "not connected" — same convention CEE's converter uses. */
    private static final double OPEN_CIRCUIT_RESISTANCE = 999_999.0;

    public ElectrolyzerElectricDevice(Level level, BlockPos pos, DevicesSavedData deviceSD, SimulatedDeviceType<?> type) {
        super(level, pos, deviceSD, type);
    }

    @Override
    public void preTick(BridgeCollector bridges) {
        bridges.builder(pos).resistor(0, 1, currentResistance());
    }

    /**
     * Dial scaling per Dado's spec: 0% = off, 50% = base, 100% = double.
     * Linear: multiplier = dial / 50. Resistance from R = V^2 / P.
     */
    private double currentResistance() {
        if (!level.isLoaded(pos))
            return OPEN_CIRCUIT_RESISTANCE;
        if (!(level.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity electrolyzer))
            return OPEN_CIRCUIT_RESISTANCE;

        int dial = electrolyzer.speed.getValue();
        if (dial <= 0)
            return OPEN_CIRCUIT_RESISTANCE;

        double targetWatts = BASE_FE_PER_TICK
                * CEEConfigs.server().wattFeTConversionRate.get()
                * (dial / 50.0);

        return (RATED_VOLTAGE * RATED_VOLTAGE) / targetWatts;
    }

    /**
     * Survive wrench rotation (facing changes) without dropping attached
     * wires; only a genuine block swap removes the device. Mirrors CEE's
     * electric motor.
     */
    @Override
    public boolean shouldRemove(BlockState oldState, BlockState newState) {
        return oldState.getBlock().getClass() != newState.getBlock().getClass();
    }
}