package com.dadoirie.assortedtweaksnfixes.mixin.etched.extendedradioblockentity;

import com.dadoirie.assortedtweaksnfixes.compat.etched.RadioNameAccess;
import com.dadoirie.assortedtweaksnfixes.compat.etched.radio.RadioMenu;
import com.dadoirie.assortedtweaksnfixes.mixin.ConditionalMixinPlugin;
import gg.moonflower.etched.common.block.RadioBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.function.Consumer;

@Mixin(RadioBlock.class)
public abstract class ExtendedRadioBlockMixin {

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    private void atnf$openExtendedMenu(BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<MenuProvider> cir) {
        if (!ConditionalMixinPlugin.isApplied("etched.ExtendedRadioBlockEntityMixin")) {
            return;
        }
        cir.setReturnValue(new SimpleMenuProvider(
                (menuId, playerInventory, player) -> new RadioMenu(menuId, ContainerLevelAccess.create(level, blockPos)),
                Component.translatable("container.etched.radio")));
    }

    @Redirect(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;"
            )
    )
    private OptionalInt atnf$openMenuWithName(Player receiver, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataWriter,
                                              ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!ConditionalMixinPlugin.isApplied("etched.ExtendedRadioBlockEntityMixin")) {
            return receiver.openMenu(menuProvider, extraDataWriter);
        }
        String name = level.getBlockEntity(pos) instanceof RadioNameAccess nameAccess ? nameAccess.atnf$getName() : null;
        return receiver.openMenu(menuProvider, buf -> {
            extraDataWriter.accept(buf);
            buf.writeUtf(name != null ? name : "");
        });
    }
}
