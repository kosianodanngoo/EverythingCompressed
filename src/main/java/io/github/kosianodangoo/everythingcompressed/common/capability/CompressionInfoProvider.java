package io.github.kosianodangoo.everythingcompressed.common.capability;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.init.ModCapabilities;
import io.github.kosianodangoo.everythingcompressed.utils.CompressionInfoUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompressionInfoProvider implements ICapabilityProvider {
    private ItemStack stack;
    private ICompressionInfo compressionInfo;
    private LazyOptional<ICompressionInfo> lazyCompressionInfo;

    public static String COMPRESSION_INFO_NBT = "compression_info";

    public CompressionInfoProvider(ItemStack stack) {
        this.stack = stack;
        ItemStack defaultSourceStack = CompressionInfoUtil.getDefaultSourceStack();
        long defaultCompressionTime = EverythingCompressedConfig.RANDOMIZE_EMPTY_SINGULARITY.get() ? 1 : 0;
        this.compressionInfo = new CompressionInfo(defaultSourceStack, defaultCompressionTime, this::save);
        load();
        lazyCompressionInfo = LazyOptional.of(() -> this.compressionInfo);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == ModCapabilities.COMPRESSION_INFO) {
            return lazyCompressionInfo.cast();
        }
        return LazyOptional.empty();
    }

    public void save() {
        CompoundTag compoundTag = stack.getOrCreateTag();
        CompoundTag compressionInfoTag = this.compressionInfo.serializeNBT();
        compoundTag.put(COMPRESSION_INFO_NBT, compressionInfoTag);
    }

    public void load() {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null && compoundTag.contains(COMPRESSION_INFO_NBT)) {
            this.compressionInfo.deserializeNBT(compoundTag.getCompound(COMPRESSION_INFO_NBT));
        }
    }
}
