package com.dadoirie.assortedtweaksnfixes.compat.electroenergetics;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.george_vi.electroenergetics.CEERegistries;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers ATNF's simulated device types into CEE's open registry.
 * Only classload this when both "electroenergetics" and "petrochem" are present.
 */
public class ATNFSimulatedDevices {

    private static final DeferredRegister<SimulatedDeviceType<?>> DEVICES =
            DeferredRegister.create(CEERegistries.SIMULATED_DEVICE_TYPE, AssortedTweaksNFixesConstants.MOD_ID);

    public static final DeferredHolder<SimulatedDeviceType<?>, SimulatedDeviceType<ElectrolyzerElectricDevice>> ELECTROLYZER =
            DEVICES.register("petrochem_electrolyzer",
                    () -> new SimulatedDeviceType<>(
                            ResourceLocation.fromNamespaceAndPath(AssortedTweaksNFixesConstants.MOD_ID, "petrochem_electrolyzer"),
                            (type, level, pos, sd) -> new ElectrolyzerElectricDevice(level, pos, sd, type)));

    public static void register(IEventBus modEventBus) {
        DEVICES.register(modEventBus);
    }
}