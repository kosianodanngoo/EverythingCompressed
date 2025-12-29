package io.github.kosianodangoo.everythingcompressed.common.item;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.client.renderer.SingularityItemRenderer;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.capability.CompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.capability.CompressionInfoProvider;
import io.github.kosianodangoo.everythingcompressed.common.init.ModCapabilities;
import io.github.kosianodangoo.everythingcompressed.common.init.ModItems;
import io.github.kosianodangoo.everythingcompressed.utils.CompressionInfoUtil;
import io.github.kosianodangoo.everythingcompressed.utils.ResourceLocationUtil;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SingularityItem extends Item {
    public SingularityItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static ItemStack fromCompressionInfo(ICompressionInfo compressionInfo) {
        return fromCompressionInfo(compressionInfo, 1);
    }

    public static ItemStack fromCompressionInfo(ICompressionInfo compressionInfo, int count) {
        ItemStack stack = new ItemStack(ModItems.SINGULARITY.get(), count);
        CompressionInfoUtil.getLazyCompressionInfo(stack).ifPresent((cap) -> cap.copyInfoFrom(compressionInfo));
        return stack;
    }

    public static ItemStack fromSourceStack(ItemStack stack) {
        return fromSourceStack(stack, 1);
    }

    public static ItemStack fromSourceStack(ItemStack sourceStack, int count) {
        CompressionInfo compressionInfo = new CompressionInfo(sourceStack);
        ICompressionInfo sourceCompressionInfo = CompressionInfoUtil.getCompressionInfo(sourceStack);
        if (sourceCompressionInfo != null) {
            compressionInfo.setCompressionTime(sourceCompressionInfo.getCompressionTime() + 1);
            compressionInfo.setSourceStack(sourceCompressionInfo.getSourceStack());
        }
        return fromCompressionInfo(compressionInfo, count);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new CompressionInfoProvider(stack);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        ItemStack sourceStack = ItemStack.EMPTY;
        long compressionTime = 0;
        ICompressionInfo compressionInfo = CompressionInfoUtil.getCompressionInfo(stack);
        if (compressionInfo != null) {
            sourceStack = compressionInfo.getSourceStack();
            compressionTime = compressionInfo.getCompressionTime();
        }
        if (compressionTime == 1) return Component.translatable("item.everything_compressed.singularity.single", sourceStack.getHoverName());
        return Component.translatable("item.everything_compressed.singularity.multiple", compressionTime, sourceStack.getHoverName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final SingularityItemRenderer renderer = new SingularityItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }
}
