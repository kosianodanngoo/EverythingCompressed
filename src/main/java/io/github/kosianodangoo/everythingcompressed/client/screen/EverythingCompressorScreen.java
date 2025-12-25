package io.github.kosianodangoo.everythingcompressed.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EverythingCompressorScreen extends AbstractContainerScreen<EverythingCompressorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EverythingCompressed.MOD_ID,"textures/gui/everything_compressor.png");

    public EverythingCompressorScreen(EverythingCompressorMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        EverythingCompressed.LOGGER.debug("create screen instance");
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 70;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int x = (width - this.imageWidth) / 2;
        int y = (height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE,x,y,0,0, 256, 256, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        this.renderBackground(graphics);
        super.render(graphics, i, i1, v);
        this.renderTooltip(graphics, i, i1);
    }
}
