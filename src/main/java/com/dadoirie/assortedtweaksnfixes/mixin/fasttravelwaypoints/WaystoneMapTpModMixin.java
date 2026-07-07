package com.dadoirie.assortedtweaksnfixes.mixin.fasttravelwaypoints;

import com.macat.waystonemap.WaystoneMapTpMod;
import com.technophobia.technonw.config.CompatConfig;
import com.technophobia.technonw.payment.CombinedPaymentService;
import com.technophobia.technonw.payment.PaymentResult;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaystoneMapTpMod.class)
public class WaystoneMapTpModMixin {

    @Inject(
            method = "completeTeleportWithFx",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedTweaks$chargeNumismaticsPayment(
            ServerPlayer player,
            ServerLevel level,
            BlockPos waystoneBottom,
            Vec3 fallbackExact,
            int levelCost,
            CallbackInfo ci
    ) {
        if (player.isCreative() || player.isSpectator()) return;
        if (!(Boolean) CompatConfig.CREATIVE_BYPASSES_COST.get()) return;

        int cost = assortedTweaks$calculateCost(player, level, waystoneBottom);
        if (cost <= 0) return;

        CombinedPaymentService paymentService = new CombinedPaymentService();
        CombinedPaymentService.ChargeState state = new CombinedPaymentService.ChargeState();
        PaymentResult result = paymentService.charge(player, cost, state);

        if (!result.success()) {
            player.displayClientMessage(
                    Component.translatable("technonw.payment.failure." + result.failureKey())
                            .withStyle(ChatFormatting.RED),
                    true
            );
            ci.cancel();
        } else {
            player.displayClientMessage(
                    Component.translatable("technonw.payment.success", cost, result.coinsSpent(), result.cardSpent()),
                    true
            );
        }
    }

    @Unique
    private static int assortedTweaks$calculateCost(ServerPlayer player, ServerLevel targetLevel, BlockPos waystoneBottom) {
        BlockPos sourcePos = player.blockPosition();
        int distance = sourcePos.distManhattan(waystoneBottom);

        int cost = CompatConfig.BASE_COST.get()
                + (int) Math.round(Math.sqrt(distance) * CompatConfig.SQRT_DISTANCE_MULTIPLIER.get())
                + (int) Math.round(distance * CompatConfig.LINEAR_DISTANCE_MULTIPLIER.get());

        boolean crossDimensional = !player.serverLevel().dimension().equals(targetLevel.dimension());
        if (crossDimensional) {
            cost += CompatConfig.INTERDIMENSIONAL_FLAT_SURCHARGE.get();
        }

        return Math.clamp(CompatConfig.MIN_COST.get(), cost,
                CompatConfig.MAX_COST.get()
        );
    }
}