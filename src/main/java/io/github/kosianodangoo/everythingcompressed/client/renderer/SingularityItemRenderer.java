package io.github.kosianodangoo.everythingcompressed.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import io.github.kosianodangoo.everythingcompressed.utils.CompressionInfoUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.ModelData;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class SingularityItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Direction[] QUAD_FACES = Arrays.copyOf(Direction.values(), Direction.values().length + 1);

    public Supplier<BakedModel> model;

    public SingularityItemRenderer(ResourceLocation resourceLocation) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        model = () -> Minecraft.getInstance().getModelManager().getModel(resourceLocation);
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

    public void renderModel(ItemStack pStack, ItemDisplayContext pContext, PoseStack pPose, MultiBufferSource pBuf, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, ICompressionInfo compressionInfo) {

        pPose.translate(0.5, 0.5, 0.5);
        BakedModel sourceStackModel = Minecraft.getInstance().getItemRenderer().getModel(compressionInfo.getSourceStack(), Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
        Minecraft.getInstance().getItemRenderer().render(compressionInfo.getSourceStack(), pContext, false, pPose, pBuf, pPackedLight, pPackedOverlay, sourceStackModel);

        BakedModel model = this.model.get();
        model = ForgeHooksClient.handleCameraTransforms(pPose, model, pContext, pContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || pContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        pPose.translate(-0.5, -0.5, -0.5);
        renderSourceModel(pStack, pContext, pPose, pVertexConsumer, pPackedLight, pPackedOverlay, compressionInfo, model);
    }

    public void renderSourceModel(ItemStack pStack, ItemDisplayContext pContext, PoseStack pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, ICompressionInfo compressionInfo, BakedModel model) {
        for (Direction face : QUAD_FACES) {
            RANDOM.setSeed(810);
            for (BakedModel pass : model.getRenderPasses(pStack, true)) {
                for (RenderType type : pass.getRenderTypes(pStack, true)) {
                    for (BakedQuad quad : pass.getQuads(null, face, RANDOM, ModelData.EMPTY, type)) {
                        float brightness = compressionInfo.getCompressionTime() != -1 ? (float) (1f / Math.sqrt(compressionInfo.getCompressionTime() + 1)) : 1;

                        pVertexConsumer.putBulkData(pPose.last(), quad, brightness, brightness, brightness, 1f, pPackedLight, pPackedOverlay, true);
                    }
                }
            }
        }
    }
}
