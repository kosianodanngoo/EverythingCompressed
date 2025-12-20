package io.github.kosianodangoo.everythingcompressed.common.capability;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.common.api.IChangeObserver;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CompressionInfo implements ICompressionInfo, IChangeObserver {
    private ItemStack sourceStack;
    private long compressionTime;
    private final Runnable onChange;

    public static String SOURCE_STACK_NBT = "source_stack";
    public static String COMPRESSION_TIME_NBT = "compression_time";

    public CompressionInfo(ItemStack sourceStack) {
        this(sourceStack, 1);
    }

    public CompressionInfo(ItemStack sourceStack, long compressionTime) {
        this(sourceStack, compressionTime, ()->{});
    }

    public CompressionInfo(ItemStack sourceStack, long compressionTime, Runnable onChange) {
        sourceStack.copyWithCount(1);
        this.sourceStack = sourceStack;
        this.compressionTime = compressionTime;
        this.onChange = onChange;
    }

    @Override
    public ItemStack getSourceStack() {
        return sourceStack;
    }

    @Override
    public long getCompressionTime() {
        return compressionTime;
    }

    @Override
    public void setSourceStack(ItemStack sourceStack) {
        this.sourceStack = sourceStack.copyWithCount(1);
        onChange();
    }

    @Override
    public void setCompressionTime(long compressionTime) {
        this.compressionTime = compressionTime;
        onChange();
    }

    @Override
    public void copyInfoFrom(ICompressionInfo compressionInfo) {
        this.setSourceStack(compressionInfo.getSourceStack());
        this.setCompressionTime(compressionInfo.getCompressionTime());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put(SOURCE_STACK_NBT, getSourceStack().serializeNBT());
        compoundTag.putLong(COMPRESSION_TIME_NBT, getCompressionTime());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        if(compoundTag.contains(SOURCE_STACK_NBT))
            setSourceStack(ItemStack.of(compoundTag.getCompound(SOURCE_STACK_NBT)));
        if(compoundTag.contains(COMPRESSION_TIME_NBT))
            setCompressionTime(compoundTag.getLong(COMPRESSION_TIME_NBT));
    }

    @Override
    public void onChange() {
        this.onChange.run();
    }
}
