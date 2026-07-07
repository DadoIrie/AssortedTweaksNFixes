package com.dadoirie.assortedtweaksnfixes.mixin.computercraft;

import dan200.computercraft.client.integration.emi.EMIComputerCraft;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.integration.RecipeModHelpers;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiStackList;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EMIComputerCraft.class)
public class EMIComputerCraftDedupRegistryMixin {

    @Final
    @Shadow
    private static Comparison turtleComparison;

    @Final
    @Shadow
    private static Comparison pocketComparison;

    /**
     * @author DadoIrie
     * @reason Prevent duplicate registration of turtles and pocket computers
     *         caused by RecipeModHelpers.getExtraStacks() adding already
     *         registered stacks to EMI.
     */
    @SuppressWarnings("DataFlowIssue")
    @Overwrite
    public void register(EmiRegistry registry) {
        registry.setDefaultComparison(ModRegistry.Items.TURTLE_NORMAL.get(), turtleComparison);
        registry.setDefaultComparison(ModRegistry.Items.TURTLE_ADVANCED.get(), turtleComparison);
        registry.setDefaultComparison(ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), pocketComparison);
        registry.setDefaultComparison(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), pocketComparison);

        for (var stack : RecipeModHelpers.getExtraStacks(Minecraft.getInstance().level.registryAccess())) {
            EmiStack emiStack = EmiStack.of(stack);

            boolean alreadyRegistered = EmiStackList.stacks.stream()
                    .anyMatch(s -> s.equals(emiStack));

            if (!alreadyRegistered) {
                registry.addEmiStack(emiStack);
            }
        }
    }
}