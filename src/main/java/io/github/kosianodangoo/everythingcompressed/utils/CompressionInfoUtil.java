package io.github.kosianodangoo.everythingcompressed.utils;

import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.init.ModCapabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class CompressionInfoUtil {
    public static LazyOptional<ICompressionInfo> getLazyCompressionInfo(ItemStack stack) {
        return stack.getCapability(ModCapabilities.COMPRESSION_INFO);
    }

    public static @Nullable ICompressionInfo getCompressionInfo(ItemStack stack) {
        return getLazyCompressionInfo(stack).orElse(null);
    }
}

