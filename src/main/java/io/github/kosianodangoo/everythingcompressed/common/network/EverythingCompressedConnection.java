package io.github.kosianodangoo.everythingcompressed.common.network;

import io.github.kosianodangoo.everythingcompressed.common.network.clientbound.ClientboundUpdateCompressedStackPacket;
import io.github.kosianodangoo.everythingcompressed.common.network.serverbound.ServerboundUpdateCompressorLockStatePacket;
import io.github.kosianodangoo.everythingcompressed.utils.ResourceLocationUtil;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class EverythingCompressedConnection {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE
            = NetworkRegistry.newSimpleChannel(ResourceLocationUtil.getResourceLocation("network"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void init() {
        int id = -1;

        INSTANCE.registerMessage(++id, ClientboundUpdateCompressedStackPacket.class, ClientboundUpdateCompressedStackPacket::encode, ClientboundUpdateCompressedStackPacket::decode, ClientboundUpdateCompressedStackPacket::handle);
        INSTANCE.registerMessage(++id, ServerboundUpdateCompressorLockStatePacket.class, ServerboundUpdateCompressorLockStatePacket::encode, ServerboundUpdateCompressorLockStatePacket::decode, ServerboundUpdateCompressorLockStatePacket::handle);
    }
}
