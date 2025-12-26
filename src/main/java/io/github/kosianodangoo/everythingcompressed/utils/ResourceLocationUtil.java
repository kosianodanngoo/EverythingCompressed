package io.github.kosianodangoo.everythingcompressed.utils;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class ResourceLocationUtil {
    public static ResourceLocation getResourceLocation(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation getResourceLocation(String path) {
        return getResourceLocation(EverythingCompressed.MOD_ID, path);
    }
}
