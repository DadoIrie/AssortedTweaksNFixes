package com.dadoirie.assortedtweaksnfixes.compat.condiments;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DyeDepotCrateRegistry {

    public static final Map<DyeColor, Supplier<Block>> DYE_CRATES = new HashMap<>();

    private DyeDepotCrateRegistry() {}

}