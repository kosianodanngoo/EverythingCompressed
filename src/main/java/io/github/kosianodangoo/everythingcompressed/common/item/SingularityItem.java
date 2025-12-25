package io.github.kosianodangoo.everythingcompressed.common.item;

import io.github.kosianodangoo.everythingcompressed.client.renderer.SingularityItemRenderer;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.capability.CompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.capability.CompressionInfoProvider;
import io.github.kosianodangoo.everythingcompressed.common.init.ModCapabilities;
import io.github.kosianodangoo.everythingcompressed.common.init.ModItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        getLazyCompressionInfo(stack).ifPresent((cap) -> cap.copyInfoFrom(compressionInfo));
        return stack;
    }

    public static ItemStack fromSourceStack(ItemStack stack) {
        return fromSourceStack(stack, 1);
    }

    public static ItemStack fromSourceStack(ItemStack sourceStack, int count) {
        CompressionInfo compressionInfo = new CompressionInfo(sourceStack);
        ICompressionInfo sourceCompressionInfo = getLazyCompressionInfo(sourceStack).orElse(null);
        if (sourceCompressionInfo != null) {
            compressionInfo.setCompressionTime(sourceCompressionInfo.getCompressionTime() + 1);
            compressionInfo.setSourceStack(sourceCompressionInfo.getSourceStack());
        }
        return fromCompressionInfo(compressionInfo, count);
    }

    public static LazyOptional<ICompressionInfo> getLazyCompressionInfo(ItemStack stack) {
        return stack.getCapability(ModCapabilities.COMPRESSION_INFO);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new CompressionInfoProvider(stack);
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readShareTag(stack, nbt);
        if(nbt.contains(CompressionInfoProvider.COMPRESSION_INFO_NBT)) {
            getLazyCompressionInfo(stack).ifPresent((cap) ->
                cap.deserializeNBT(nbt.getCompound(CompressionInfoProvider.COMPRESSION_INFO_NBT))
            );
        }
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag compoundTag = super.getShareTag(stack);
        getLazyCompressionInfo(stack).ifPresent((cap) ->
            compoundTag.put(CompressionInfoProvider.COMPRESSION_INFO_NBT, cap.serializeNBT())
        );
        return compoundTag;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        ItemStack sourceStack = ItemStack.EMPTY;
        long compressionTime = 0;
        ICompressionInfo compressionInfo = getLazyCompressionInfo(stack).orElse(null);
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
            private SingularityItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer == null ? this.renderer = new SingularityItemRenderer(SingularityItem.this) : this.renderer;
            }
        });
    }
}
