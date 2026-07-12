package com.dadoirie.assortedtweaksnfixes.content.hammer;

import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HammerItem extends Item {

    public HammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        return use(context.getLevel(), player, context.getHand()).getResult();
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 9, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -1.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionHand otherHand = InteractionHand.values()[(~hand.ordinal()) & 1];
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack itemInOtherHand = player.getItemInHand(otherHand);

        HammerRecipe.HammerInv hammerInv = new HammerRecipe.HammerInv(itemInOtherHand);
        Optional<RecipeHolder<HammerRecipe>> recipe = level.getRecipeManager().getRecipeFor(HammerFeature.HAMMERING.getType(), hammerInv, level);
        if (recipe.isPresent()) {
            ItemStack processingItem = itemInOtherHand.copy();
            itemInOtherHand.shrink(1);
            processingItem.setCount(1);

            itemInHand.set(HammerFeature.PROCESSING_ITEM.get(), new SandPaperItemComponent(processingItem));
            player.startUsingItem(hand);
            return InteractionResultHolder.success(itemInHand);
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player))
            return stack;
        synchronized (HammerItem.class) {
            if (!stack.has(HammerFeature.PROCESSING_ITEM.get()))
                return stack;
            ItemStack processingItem = Objects.requireNonNull(stack.get(HammerFeature.PROCESSING_ITEM.get())).item();

            HammerRecipe.HammerInv hammerInv = new HammerRecipe.HammerInv(processingItem);
            Optional<RecipeHolder<HammerRecipe>> recipe = level.getRecipeManager().getRecipeFor(HammerFeature.HAMMERING.getType(), hammerInv, level);

            stack.remove(HammerFeature.PROCESSING_ITEM.get());

            if (recipe.isEmpty()) {
                player.getInventory().placeItemBackInInventory(processingItem);
                return stack;
            }

            List<ItemStack> results = recipe.get().value().rollResults(level.random);
            if (results.isEmpty()) {
                if (level.isClientSide) {
                    level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1f, 1f, false);
                } else {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1f, 1f);
                }
            } else {
                for (ItemStack result : results) {
                    player.getInventory().placeItemBackInInventory(result.copy());
                }
            }
            if (level instanceof ServerLevel sl)
                stack.hurtAndBreak(1, sl, player, i -> {});
            return stack;
        }
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int tick) {
        if (AnimationTickHolder.getTicks() % 10 == 0) {
            level.playLocalSound(entity.xo, entity.yo, entity.zo, SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 0.3f, 1f, true);

            if (!stack.has(HammerFeature.PROCESSING_ITEM.get())) {
                super.onUseTick(level, entity, stack, tick);
                return;
            }

            ItemStack processingItem = Objects.requireNonNull(stack.get(HammerFeature.PROCESSING_ITEM.get())).item();

            for (int i = 0; i < 30; i++) {
                Vec3 offset = VecHelper.offsetRandomly(entity.position().add(Math.sin(-entity.getYRot() / 180 * Math.PI) / 2, 1.3, Math.cos(-entity.getYRot() / 180 * Math.PI) / 2), level.getRandom(), .3f);
                Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.getRandom(), .1f);

                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, processingItem), offset.x(), offset.y(),
                        offset.z(), motion.x(), motion.y(), motion.z());
            }
        }
        super.onUseTick(level, entity, stack, tick);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int tick) {
        synchronized (HammerItem.class) {
            if (!(entity instanceof Player player))
                return;
            if (!stack.has(HammerFeature.PROCESSING_ITEM.get()))
                return;

            ItemStack processingItem = Objects.requireNonNull(stack.get(HammerFeature.PROCESSING_ITEM.get())).item();
            player.getInventory().placeItemBackInInventory(processingItem);
            stack.remove(HammerFeature.PROCESSING_ITEM.get());
        }
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        releaseUsing(stack, entity.level(), entity, 0);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 90;
    }
}