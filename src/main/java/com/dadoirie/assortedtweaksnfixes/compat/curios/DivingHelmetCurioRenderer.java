package com.dadoirie.assortedtweaksnfixes.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Curios "head" slot renderer for Create's copper/netherite diving helmets. Reuses the same
 * humanoid armor texture (layer_1) that {@code BaseArmorItem} renders for the vanilla head slot,
 * since neither variant implements {@code LayeredArmorItem}.
 */
public class DivingHelmetCurioRenderer implements ICurioRenderer {

    private HumanoidModel<LivingEntity> armorModel;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
                                                                           PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent,
                                                                           MultiBufferSource bufferSource, int light, float limbSwing,
                                                                           float limbSwingAmount, float partialTicks, float ageInTicks,
                                                                           float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        if (!(stack.getItem() instanceof DivingHelmetItem item)) return;

        M parentModel = renderLayerParent.getModel();
        if (!(parentModel instanceof HumanoidModel<?> humanoidModel)) return;

        ArmorMaterial.Layer materialLayer = item.getMaterial().value().layers().get(0);
        ResourceLocation texture = item.getArmorTexture(stack, entity, EquipmentSlot.HEAD, materialLayer, false);
        if (texture == null) return;

        HumanoidModel<LivingEntity> model = getArmorModel();
        model.young = humanoidModel.young;
        model.setAllVisible(false);
        model.head.copyFrom(humanoidModel.head);
        model.hat.copyFrom(humanoidModel.hat);
        model.head.visible = true;
        model.hat.visible = true;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
        if (stack.hasFoil()) {
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY, -1);
        }
    }

    private HumanoidModel<LivingEntity> getArmorModel() {
        if (armorModel == null) {
            armorModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        }
        return armorModel;
    }
}
