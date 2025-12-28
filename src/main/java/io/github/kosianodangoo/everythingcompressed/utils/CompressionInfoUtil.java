package io.github.kosianodangoo.everythingcompressed.utils;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.capability.CompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.init.ModCapabilities;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CompressionInfoUtil {
    public static final RandomSource RANDOM = RandomSource.create();

    public static LazyOptional<ICompressionInfo> getLazyCompressionInfo(ItemStack stack) {
        return stack.getCapability(ModCapabilities.COMPRESSION_INFO);
    }

    public static @Nullable ICompressionInfo getCompressionInfo(ItemStack stack) {
        return getLazyCompressionInfo(stack).orElse(null);
    }

    public static Optional<ItemStack> getCompressedStack(ItemStack stack) {
        ICompressionInfo compressionInfo = getCompressionInfo(stack);
        if (compressionInfo == null) {
            return Optional.empty();
        }
        long compressionTime = compressionInfo.getCompressionTime();
        ItemStack sourceStack = compressionInfo.getSourceStack();
        if (compressionTime <= 1) {
            return Optional.of(sourceStack);
        }
        return Optional.of(SingularityItem.fromCompressionInfo(new CompressionInfo(sourceStack, compressionTime - 1)));
    }

    public static ItemStack getDefaultSourceStack() {
        if (!EverythingCompressedConfig.RANDOMIZE_EMPTY_SINGULARITY.get()) {
            return ItemStack.EMPTY;
        }
        long millis = Util.getMillis();
        List<Item> items = ForgeRegistries.ITEMS.getValues().stream().toList();
        RANDOM.setSeed(millis / 200);
        return items.get(RANDOM.nextInt(items.size())).getDefaultInstance();
    }
}

