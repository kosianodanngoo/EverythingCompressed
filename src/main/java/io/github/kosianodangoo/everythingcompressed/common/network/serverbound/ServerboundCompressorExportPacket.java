package io.github.kosianodangoo.everythingcompressed.common.network.serverbound;

import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundCompressorExportPacket {
    private final int menuId;

    public ServerboundCompressorExportPacket(int menuId) {
        this.menuId = menuId;
    }

    public static void encode(ServerboundCompressorExportPacket message, FriendlyByteBuf buf) {
        buf.writeInt(message.menuId);
    }

    public static ServerboundCompressorExportPacket decode(FriendlyByteBuf buf) {
        return new ServerboundCompressorExportPacket(buf.readInt());
    }

    public static void handle(ServerboundCompressorExportPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof EverythingCompressorMenu menu) {
                    menu.blockEntity.exportCompressedStack();
                }
            });
        }
        context.setPacketHandled(true);
    }
}
