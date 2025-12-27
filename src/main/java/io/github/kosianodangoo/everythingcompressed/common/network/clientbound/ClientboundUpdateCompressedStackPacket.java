package io.github.kosianodangoo.everythingcompressed.common.network.clientbound;

import io.github.kosianodangoo.everythingcompressed.common.block.entity.EverythingCompressorBlockEntity;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class ClientboundUpdateCompressedStackPacket {
    public final ItemStack stack;
    public final BlockPos pos;

    public ClientboundUpdateCompressedStackPacket(ItemStack stack, BlockPos pos) {
        this.stack = stack;
        this.pos = pos;
    }

    public static void encode(ClientboundUpdateCompressedStackPacket message, FriendlyByteBuf buf) {
        buf.writeItemStack(message.stack, false);
        buf.writeBlockPos(message.pos);
    }

    public static ClientboundUpdateCompressedStackPacket decode(FriendlyByteBuf buf) {
        return new ClientboundUpdateCompressedStackPacket(buf.readItem(), buf.readBlockPos());
    }

    public static void handle(ClientboundUpdateCompressedStackPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                ClientLevel level = mc.level;
                if (level == null) {
                    return;
                }
                Optional<EverythingCompressorBlockEntity> optionalBlockEntity = level.getBlockEntity(message.pos, ModBlockEntityTypes.EVERYTHING_COMPRESSOR.get());
                optionalBlockEntity.ifPresent((blockEntity) ->
                    blockEntity.setCompressedStack(message.stack)
                );
            });
        }
    }
}
