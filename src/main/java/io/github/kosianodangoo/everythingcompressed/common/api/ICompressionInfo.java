package io.github.kosianodangoo.everythingcompressed.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICompressionInfo extends INBTSerializable<CompoundTag> {
    ItemStack getSourceStack();
    long getCompressionTime();

    void setSourceStack(ItemStack sourceStack);
    void setCompressionTime(long compressionTime);

    void copyInfoFrom(ICompressionInfo compressionInfo);
}
