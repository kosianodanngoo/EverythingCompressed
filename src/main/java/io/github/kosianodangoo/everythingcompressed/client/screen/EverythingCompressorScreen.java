package io.github.kosianodangoo.everythingcompressed.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import io.github.kosianodangoo.everythingcompressed.utils.ResourceLocationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class EverythingCompressorScreen extends AbstractContainerScreen<EverythingCompressorMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocationUtil.getResourceLocation("textures/gui/everything_compressor.png");

    public EverythingCompressorScreen(EverythingCompressorMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
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
        long required = menu.getRequired();
        if(required > 0)
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("gui.everything_compressed.everything_compressor.required", required), imageWidth/2 + x, 16 + y, 4210752);

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("gui.everything_compressed.everything_compressor.progress", menu.getProgress()), imageWidth/2 + x, 26 + y, 4210752);

        ItemStack compressedStack = menu.getCompressedStack();
        if (!compressedStack.isEmpty())
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("gui.everything_compressed.everything_compressor.compressing", compressedStack.getDisplayName()), imageWidth/2 + x, 36 + y, 4210752);

        guiGraphics.blit(TEXTURE,x,y,0,0, 256, 256, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int i1, float v) {
        this.renderBackground(graphics);
        super.render(graphics, i, i1, v);
        this.renderTooltip(graphics, i, i1);
    }
}
