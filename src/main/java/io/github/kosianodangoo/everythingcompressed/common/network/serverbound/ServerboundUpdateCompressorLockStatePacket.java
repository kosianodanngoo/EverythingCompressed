package io.github.kosianodangoo.everythingcompressed.common.network.serverbound;

import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundUpdateCompressorLockStatePacket {
    private final boolean isLocked;
    private final int menuId;

    public ServerboundUpdateCompressorLockStatePacket(boolean isLocked, int menuId) {
        this.isLocked = isLocked;
        this.menuId = menuId;
    }

    public static void encode(ServerboundUpdateCompressorLockStatePacket message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.isLocked);
        buf.writeInt(message.menuId);
    }

    public static ServerboundUpdateCompressorLockStatePacket decode(FriendlyByteBuf buf) {
        return new ServerboundUpdateCompressorLockStatePacket(buf.readBoolean(), buf.readInt());
    }

    public static void handle(ServerboundUpdateCompressorLockStatePacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer entity = context.getSender();
                if (entity == null) {
                    return;
                }
                if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof EverythingCompressorMenu menu) {
                    menu.blockEntity.setLocked(message.isLocked);
                }
            });
        }
        context.setPacketHandled(true);
    }
}
