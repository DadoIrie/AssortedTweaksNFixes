package com.dadoirie.assortedtweaksnfixes.mixin.technonw;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixes;
import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.technophobia.technonw.config.CompatConfig;
import com.technophobia.technonw.payment.BankCardPaymentService;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.bank.CardItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

@Mixin(BankCardPaymentService.class)
public class BankCardPaymentServiceMixin {
    private static final Logger LOGGER = ATNFConstants.getLogger(AssortedTweaksNFixes.class);

    @Inject(
            method = "findFirstBoundAccount",
            at = @At("RETURN"),
            cancellable = true
    )
    private void assortedTweaks$findCurioCard(Player player, CallbackInfoReturnable<UUID> cir) {
        if (cir.getReturnValue() != null) return;

        CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
            var matches = curiosInventory.findCurios("card");
            if (matches.isEmpty()) return;
            ItemStack card = matches.get(0).stack();
            if (CardItem.isBound(card)) {
                UUID accountId = CardItem.get(card);
                if (accountId != null) {
                    cir.setReturnValue(accountId);
                }
            }
        });
    }

    @Inject(
            method = "getAvailable",
            at = @At("HEAD"),
            cancellable = true
    )
    private void assortedTweaks$guardClientBankAccess(UUID accountId, CallbackInfoReturnable<Integer> cir) {
        if (FMLEnvironment.dist.isClient()) {
            LOGGER.info("getAvailable called on client, isSameThread={}, accountId={}",
                    Minecraft.getInstance().isSameThread(), accountId);
            cir.setReturnValue(CompatConfig.MAX_COST.get());
            return;
        }
        BankAccount account = Numismatics.BANK.getAccount(accountId);
        cir.setReturnValue(account != null ? account.getBalance() : 0);
    }
}