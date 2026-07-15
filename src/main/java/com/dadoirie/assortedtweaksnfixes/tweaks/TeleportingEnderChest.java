package com.dadoirie.assortedtweaksnfixes.tweaks;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.concurrent.ThreadLocalRandom;

@EventBusSubscriber(modid = ATNFConstants.MOD_ID)
public class TeleportingEnderChest {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        if (!state.is(Blocks.ENDER_CHEST)) return;

        Player player = event.getPlayer();
        if (player.isCreative() || hasSilkTouch(player.getMainHandItem(), event.getLevel())) return;

        event.setCanceled(true);

        BlockPos pos = event.getPos();
        LevelAccessor level = event.getLevel();

        int origX = pos.getX();
        int origY = pos.getY();
        int origZ = pos.getZ();

        int startIndex = ThreadLocalRandom.current().nextInt(243);

        BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 243; i++) {
            int index = (i + startIndex) % 243;

            int tx = origX + (index % 9) - 4;
            int ty = origY + ((index / 9) % 3) - 1;
            int tz = origZ + (index / 27) - 4;

            if (tx == origX && ty == origY && tz == origZ) continue;

            target.set(tx, ty, tz);
            below.set(tx, ty - 1, tz);

            if (level.getBlockState(target).isAir() &&
                    level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {

                level.destroyBlock(pos, false);
                level.setBlock(target, state, 3);
                return;
            }
        }

        level.destroyBlock(pos, true);
    }

    private static boolean hasSilkTouch(ItemStack stack, LevelAccessor level) {
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> silkTouch = registry.getOrThrow(Enchantments.SILK_TOUCH);
        return stack.getEnchantmentLevel(silkTouch) > 0;
    }
}