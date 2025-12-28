package io.github.kosianodangoo.everythingcompressed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.utils.CompressionInfoUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.logging.Level;

@OnlyIn(Dist.CLIENT)
public class SingularityItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final RandomSource RANDOM = RandomSource.create();
    public SingularityItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pContext, PoseStack pPose, MultiBufferSource pBuf, int pPackedLight, int pPackedOverlay) {
        VertexConsumer buffer = ItemRenderer.getFoilBuffer(pBuf, Sheets.translucentCullBlockSheet(), true, pStack.hasFoil());
        ICompressionInfo compressionInfo = CompressionInfoUtil.getCompressionInfo(pStack);
        if (compressionInfo == null) {
            super.renderByItem(pStack, pContext, pPose, pBuf, pPackedLight, pPackedOverlay);
            return;
        }

        renderModel(pStack, pContext, pPose, pBuf, buffer, pPackedLight, pPackedOverlay, compressionInfo);
    }

    public static int darkenLight(int light, double brightness) {
        int sl = light >> 16 & 0xff;
        int bl = light & 0xff;
        sl = (int) (sl * brightness) & 0xff;
        bl = (int) (bl * brightness) & 0xff;
        return sl << 16 | bl;
    }

    public void renderModel(ItemStack pStack, ItemDisplayContext pContext, PoseStack pPose, MultiBufferSource pBuf, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, ICompressionInfo compressionInfo) {
        long millis = Util.getMillis();
        double time = (double) (millis % 3000 - 1500) / 1000;
        double delta = time * time * time * time;

        ClientLevel level = Minecraft.getInstance().level;
        RandomSource random = level != null ? level.getRandom() : RANDOM;
        double randomScaleMultiplier = 0.1;
        double randomScaling = (randomScaleMultiplier * random.nextGaussian()) + 1;

        double brightness = compressionInfo.getCompressionTime() != -1 ? (float) (1f / Math.sqrt(compressionInfo.getCompressionTime() + 1)) : 1;
        brightness *= Math.abs(Math.cos(delta));

        pPose.pushPose();
        pPose.translate(0.5, 0.5, 0.5);

        double multiplier = 0.05;
        pPose.translate(multiplier * random.nextGaussian(), multiplier * random.nextGaussian(), multiplier * random.nextGaussian());
        float size = (2 - Math.abs((float)Math.cos(delta))) * (float) randomScaling;
        pPose.scale(size, size, size);
        BakedModel sourceStackModel = Minecraft.getInstance().getItemRenderer().getModel(compressionInfo.getSourceStack(), Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
        Minecraft.getInstance().getItemRenderer().render(compressionInfo.getSourceStack(), pContext, false, pPose, pBuf, darkenLight(pPackedLight, brightness), pPackedOverlay, sourceStackModel);
        pPose.popPose();
    }
}
