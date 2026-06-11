    package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

    import net.minecraft.core.BlockPos;
    import net.minecraft.world.item.ItemStack;
    import net.minecraft.world.item.crafting.RecipeHolder;
    import net.minecraft.world.item.crafting.SingleRecipeInput;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.level.block.AbstractFurnaceBlock;
    import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
    import net.minecraft.world.level.block.state.BlockState;
    import net.neoforged.neoforge.capabilities.Capabilities;
    import net.neoforged.neoforge.fluids.capability.IFluidHandler;
    import org.spongepowered.asm.mixin.Mixin;
    import org.spongepowered.asm.mixin.injection.At;
    import org.spongepowered.asm.mixin.injection.Inject;
    import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

    @Mixin(AbstractFurnaceBlockEntity.class)
    public class AbstractFurnaceBlockEntityMixin {

        @Inject(method = "serverTick", at = @At("TAIL"))
        private static void assortedtweaks_fluidFuel(
                Level level, BlockPos pos, BlockState state,
                AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {

            if (blockEntity.litTime > 0) return;

            ItemStack input = blockEntity.getItem(0);
            if (input.isEmpty()) return;

            RecipeHolder<?> recipe = blockEntity.quickCheck
                    .getRecipeFor(new SingleRecipeInput(input), level)
                    .orElse(null);
            if (recipe == null) return;

            ItemStack output = blockEntity.getItem(2);
            if (!output.isEmpty()) {
                ItemStack recipeOutput = recipe.value().getResultItem(level.registryAccess());
                if (!ItemStack.isSameItemSameComponents(output, recipeOutput) ||
                        output.getCount() + recipeOutput.getCount() > output.getMaxStackSize()) return;
            }

            IFluidHandler handler = level.getCapability(
                    Capabilities.FluidHandler.BLOCK,
                    pos, state, blockEntity, null);
            if (handler == null) return;

            int fuelAvailable = handler.getFluidInTank(0).getAmount();
            if (fuelAvailable <= 0) return;

            int cookTimeTicks = ((AbstractFurnaceBlockEntityAccessor) blockEntity).getCookingTotalTime();
            int totalBurnTime = 20000;
            int itemCount = Math.min(input.getCount(), 64);

            int totalTicksNeeded = itemCount * cookTimeTicks;
            int mbNeeded = (int) Math.ceil((double) (totalTicksNeeded * 1000) / totalBurnTime);

            if (fuelAvailable < mbNeeded) {
                double ticksPerMb = (double) totalBurnTime / 1000.0;
                int availableTicks = (int) (fuelAvailable * ticksPerMb);
                itemCount = availableTicks / cookTimeTicks;
                totalTicksNeeded = itemCount * cookTimeTicks;
                mbNeeded = (int) Math.ceil((double) (totalTicksNeeded * 1000) / totalBurnTime);
            }

            if (itemCount <= 0 || mbNeeded <= 0) return;

            double burnTicksPerMb = (double) totalBurnTime / 1000.0;
            int totalFuelTicks = (int) (mbNeeded * burnTicksPerMb);

            handler.drain(mbNeeded, IFluidHandler.FluidAction.EXECUTE);
            blockEntity.litTime = totalFuelTicks + 1;
            blockEntity.litDuration = totalFuelTicks + 1 ;

            if (!state.getValue(AbstractFurnaceBlock.LIT)) {
                level.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), 3);
            }
            blockEntity.setChanged();
        }
    }