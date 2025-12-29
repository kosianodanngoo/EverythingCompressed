package io.github.kosianodangoo.everythingcompressed.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.function.IntSupplier;

public class IconButton extends ExtendedButton {
    private ResourceLocation iconLocation;
    public int xOnTex;
    public int yOnTex;

    public IconButton(int xPos, int yPos, int width, int height, OnPress handler, ResourceLocation location, int x, int y) {
        super(xPos, yPos, width, height, Component.empty(), handler);
        this.iconLocation = location;
        this.xOnTex = x;
        this.yOnTex = y;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(iconLocation, this.getX(), this.getY(), xOnTex, yOnTex, this.width, this.height);
    }
}
