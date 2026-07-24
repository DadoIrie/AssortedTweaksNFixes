package com.dadoirie.assortedtweaksnfixes.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.foundation.item.LayeredArmorItem;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Shared Curios back-slot renderer for Create's backtank items and Create Jetpack's jetpack items
 * (JetpackItem extends BacktankItem). Replicates {@code BacktankArmorLayer}'s 3D tank/cogs/shaft
 * geometry plus the humanoid chestplate texture(s) that {@code BaseArmorItem}/{@code LayeredArmorItem}
 * render for the vanilla chest slot, so the look matches whether worn in the chest slot or here.
 */
public class BacktankCurioRenderer implements ICurioRenderer {

    /**
     * Whether to also draw the backtank/jetpack's own neck/torso strap texture on top of the 3D
     * tank, for netherite variants only (copper never has this overlay). Purely cosmetic to this
     * item - has no effect on the player's actual equipped chestplate, which vanilla renders
     * separately regardless of this flag. Hardcoded until there's a proper config option for it.
     */
    private static final boolean RENDER_CHEST = false;

    private HumanoidModel<LivingEntity> outerArmorModel;
    private HumanoidModel<LivingEntity> innerArmorModel;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
                                                                           PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent,
                                                                           MultiBufferSource bufferSource, int light, float limbSwing,
                                                                           float limbSwingAmount, float partialTicks, float ageInTicks,
                                                                           float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        if (entity.getPose() == Pose.SLEEPING) return;
        if (!(stack.getItem() instanceof BacktankItem item)) return;

        M parentModel = renderLayerParent.getModel();
        if (!(parentModel instanceof HumanoidModel<?> humanoidModel)) return;

        renderTank(item, entity, humanoidModel, stack, poseStack, bufferSource, light);
        if (RENDER_CHEST) {
            renderArmorPlate(item, entity, humanoidModel, stack, poseStack, bufferSource, light);
        }
    }

    private void renderTank(BacktankItem item, LivingEntity entity, HumanoidModel<?> model, ItemStack stack,
                             PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        BlockState renderedState = item.getBlock().defaultBlockState()
                .setValue(BacktankBlock.HORIZONTAL_FACING, Direction.SOUTH);
        SuperByteBuffer backtank = CachedBuffers.block(renderedState);
        SuperByteBuffer shaft = CachedBuffers.partial(BacktankRenderer.getShaftModel(renderedState), renderedState);
        SuperByteBuffer cogs = CachedBuffers.partial(BacktankRenderer.getCogsModel(renderedState), renderedState);

        VertexConsumer vertexConsumer =
                ItemRenderer.getFoilBuffer(bufferSource, Sheets.cutoutBlockSheet(), false, stack.hasFoil());

        poseStack.pushPose();

        model.body.translateAndRotate(poseStack);
        poseStack.translate(-1 / 2f, 10 / 16f, 1f);
        poseStack.scale(1, -1, -1);

        backtank.disableDiffuse()
                .light(light)
                .renderInto(poseStack, vertexConsumer);

        shaft.disableDiffuse()
                .translate(0, -3f / 16, 0)
                .light(light)
                .renderInto(poseStack, vertexConsumer);

        cogs.center()
                .rotateYDegrees(180)
                .uncenter()
                .translate(0, 6.5f / 16, 11f / 16)
                .rotate(AngleHelper.rad(2 * AnimationTickHolder.getRenderTime(entity.level()) % 360), Direction.EAST)
                .translate(0, -6.5f / 16, -11f / 16);

        cogs.disableDiffuse()
                .light(light)
                .renderInto(poseStack, vertexConsumer);

        poseStack.popPose();
    }
    
    private void renderArmorPlate(BacktankItem item, LivingEntity entity, HumanoidModel<?> parentModel, ItemStack stack,
                                   PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        boolean glint = stack.hasFoil();

        HumanoidModel<LivingEntity> outer = getOuterArmorModel();
        copyChestParts(parentModel, outer);
        ArmorMaterial.Layer materialLayer = item.getMaterial().value().layers().get(0);
        ResourceLocation outerTexture = item.getArmorTexture(stack, entity, EquipmentSlot.CHEST, materialLayer, false);
        if (outerTexture != null) {
            renderPlateLayer(outer, outerTexture, poseStack, bufferSource, light, glint);
        }

        if (item instanceof LayeredArmorItem layered) {
            HumanoidModel<LivingEntity> inner = getInnerArmorModel();
            copyChestParts(parentModel, inner);
            ResourceLocation innerTexture =
                    ResourceLocation.parse(layered.getArmorTextureLocation(entity, EquipmentSlot.CHEST, stack, 2));
            renderPlateLayer(inner, innerTexture, poseStack, bufferSource, light, glint);
        }
    }

    private void copyChestParts(HumanoidModel<?> source, HumanoidModel<LivingEntity> target) {
        target.young = source.young;
        target.setAllVisible(false);
        target.body.copyFrom(source.body);
        target.rightArm.copyFrom(source.rightArm);
        target.leftArm.copyFrom(source.leftArm);
        target.body.visible = true;
        target.rightArm.visible = true;
        target.leftArm.visible = true;
    }

    private void renderPlateLayer(HumanoidModel<LivingEntity> model, ResourceLocation texture, PoseStack poseStack,
                                   MultiBufferSource bufferSource, int light, boolean glint) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
        if (glint) {
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY, -1);
        }
    }

    private HumanoidModel<LivingEntity> getOuterArmorModel() {
        if (outerArmorModel == null) {
            outerArmorModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        }
        return outerArmorModel;
    }

    private HumanoidModel<LivingEntity> getInnerArmorModel() {
        if (innerArmorModel == null) {
            innerArmorModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        }
        return innerArmorModel;
    }
}
