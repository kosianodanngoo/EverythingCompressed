package io.github.kosianodangoo.everythingcompressed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SingularityItemRenderer extends BlockEntityWithoutLevelRenderer {


    public SingularityItemRenderer(Item item) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pContext, PoseStack pPose, MultiBufferSource pBuf, int pPackedLight, int pPackedOverlay) {
        ICompressionInfo compressionInfo = SingularityItem.getLazyCompressionInfo(pStack).orElse(null);
        if (compressionInfo == null) {
            super.renderByItem(pStack, pContext, pPose, pBuf, pPackedLight, pPackedOverlay);
            return;
        }
        pPackedLight /= (int) compressionInfo.getCompressionTime();
        super.renderByItem(pStack, pContext, pPose, pBuf, pPackedLight, pPackedOverlay);
    }
}
