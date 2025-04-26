package com.kjmaster.inventorygenerators.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class InventoryGeneratorRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        matrixStack.pushPose();

        ICurioRenderer.translateIfSneaking(matrixStack, slotContext.entity());
        ICurioRenderer.rotateIfSneaking(matrixStack, slotContext.entity());

        matrixStack.scale(0.5f, 0.5f, 0.5f);

        if (slotContext.index() == 0) {
            matrixStack.translate(0.5F, 1.5F, 0F);
        } else {
            matrixStack.translate(-0.5F, 1.5F, 0F);
        }

        matrixStack.mulPose(Axis.XP.rotationDegrees(180));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(0));

        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, slotContext.entity().level(), slotContext.entity().getId());

        matrixStack.popPose();
    }
}
